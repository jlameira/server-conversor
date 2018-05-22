package br.com.server.conversor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.server.conversor.Configuration;

@Controller
public class ConversorController {
	
	@GetMapping(value="/")
	public String index() {
		Configuration.logger.info("Acessando Index");
		return "index";
	}

}
