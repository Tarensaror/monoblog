package de.hska.lkit.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GreetingController {

	@RequestMapping(value = "/")
	public void greetingSubmit(@ModelAttribute Monoblog monoblog, Model model) {
	}
}
