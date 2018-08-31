package cn.org.hentai.desktop.controller;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by matrixy on 2017/12/13.
 */
public class BaseController
{
    @Autowired
    HttpServletRequest request;

    @Autowired
    HttpServletResponse response;
}
