package org.seckill.web;

import java.util.Date;
import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecutor;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by chizhou on 12/19/17.
 */

@Controller //类似于@Service @Component， 放入容器中
@RequestMapping("/seckill") //URL：/模块/资源/{id}/细分 /seckill/list
public class SeckillController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        // 获取列表页
        List<Seckill> seckillList = seckillService.getSeckillList();
        model.addAttribute("list", seckillList);

        // list.jsp 提供页面模版，model提供数据
        // list.jsp + mode = ModelAndView
        return "list"; // 相当于return /WEB-INF/jsp/"list".jsp
    }

    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }

        Seckill seckill = seckillService.getById(seckillId);

        if (seckill == null) {
            return "forward:/seckill/list";
        }

        model.addAttribute("seckill", seckill);
        return "detail";
    }

    // 只接受POST类型，也就是直接在浏览器中输入该网址是无效的
    // produces - application/json: 用来解决json格式混乱
    // produces - charset=UTF-8: 用来解决中文乱码，如果返回有中文的话
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json; charset=UTF-8"})
    //ajax接口，返回json, 所以不需要argument：model
    @ResponseBody //用于标示返回为json类型
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
        SeckillResult<Exposer> result;

        try {
            Exposer exposer = seckillService.exposeSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }

        return result;
    }

    @RequestMapping(value = "/{seckillId}/{md5}/execute",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecutor> execute(@PathVariable("seckillId") Long seckillId,
                                                  @PathVariable("md5") String md5,
                                                  @CookieValue(value = "killPhone", required =
                                                          false) Long userPhone) {

        //当需要验证的变量很多时，可以采用springMVC valid
        if (userPhone == null) {
            return new SeckillResult<SeckillExecutor>(false, "未注册");
        }

        try {
            SeckillExecutor seckillExecutor = seckillService.executeSeckill(seckillId, userPhone,
                    md5);
            return new SeckillResult<SeckillExecutor>(true, seckillExecutor);
        } catch (RepeatKillException e) {
            SeckillExecutor seckillExecutor = new SeckillExecutor(seckillId, SeckillStateEnum
                    .REPEAT);
            return new SeckillResult<SeckillExecutor>(true, seckillExecutor);
        } catch (SeckillCloseException e) {
            SeckillExecutor seckillExecutor = new SeckillExecutor(seckillId, SeckillStateEnum
                    .END);
            return new SeckillResult<SeckillExecutor>(true, seckillExecutor);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            SeckillExecutor seckillExecutor = new SeckillExecutor(seckillId, SeckillStateEnum
                    .INTERNAL_ERROR);
            return new SeckillResult<SeckillExecutor>(true, seckillExecutor);
        }
    }

    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time() {
        Date now = new Date();
        return new SeckillResult<Long>(true, now.getTime());
    }
}
