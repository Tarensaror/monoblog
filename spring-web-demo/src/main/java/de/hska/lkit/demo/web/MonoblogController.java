package de.hska.lkit.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MonoblogController {

	@RequestMapping(value = "/lala")
	public String test(@ModelAttribute Monoblog monoblog, Model model) {
	
	return "monoblog";
	}
}
