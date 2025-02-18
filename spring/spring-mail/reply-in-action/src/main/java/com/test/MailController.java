package com.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailController {

    @Autowired
    private MailService mailService;

    @GetMapping("/fetch-mail")
    public String fetchMails() {
        try {
            mailService.fetchMails();
            return "邮件收取成功";
        } catch (Exception e) {
            e.printStackTrace();
            return "邮件收取失败: " + e.getMessage();
        }
    }

    @GetMapping("/fetch-mail-chain")
    public String fetchMailChain() {
        try {
            mailService.fetchMailChain();
            return "邮件 chain 收取成功";
        } catch (Exception e) {
            e.printStackTrace();
            return "邮件收取失败: " + e.getMessage();
        }
    }
}