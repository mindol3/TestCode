package com.example.sample_todo.controller;

import com.example.sample_todo.Service.TodoService;
import com.example.sample_todo.dto.TodoDTO;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "todoModifyController", value ="/todo/modify")
@Log4j2
public class TodoModifyController extends HttpServlet {
    private TodoService todoService = TodoService.INSTANCE;
    private final DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8"); // 한글

        String finishedStr = req.getParameter("finished");
        TodoDTO todoDTO = TodoDTO.builder()
                .tno (Long.parseLong (req.getParameter("tno")))
                .title(req.getParameter("title"))
                .dueDate(LocalDate.parse(req.getParameter("dueDate"),DATEFORMATTER ))
                        .finished ( finishedStr !=null && finishedStr.equals("on") )
                        .build();
        log.info("/todo/modify POST...");
        log.info(todoDTO);

        try {
            todoService.modify(todoDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.sendRedirect("/todo/list");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("/todo/modify GET ...");

        try {
            Long tno = Long.parseLong(req.getParameter("tno"));
            TodoDTO todoDTO = todoService.get(tno);

            //데이터 담기
            req.setAttribute("dto", todoDTO);
            req.getRequestDispatcher("/WEB-INF/todo/modify.jsp").forward(req, resp);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ServletException("modify get... error");
        }
    }
}