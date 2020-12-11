package ru.sibdigital.proccovid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.proccovid.service.StatisticService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RestController
public class HelpController {
    @GetMapping(value = "/help/statistic")
    public List<Map<String, Object>> getHelpPage() {
        List<Map<String, Object>> result = new ArrayList<>();

        result.add(new HashMap<>() {{
            put("id", (Object) 1);
            put("global_parent_id", (Object) 1);
            put("parent_id", (Object) null);
            put("typename", (Object) "dropdown-header");
            put("title", (Object) "Название");
            put("description", (Object) "description");
        }});

        result.add(new HashMap<>() {{
            put("id", (Object) 2);
            put("global_parent_id", (Object) 1);
            put("parent_id", (Object) 1);
            put("typename", (Object) "dropdown-title");
            put("title", (Object) "title");
            put("description", (Object) "description");
        }});

        result.add(new HashMap<>() {{
            put("id", (Object) 3);
            put("global_parent_id", (Object) 1);
            put("parent_id", (Object) 2);
            put("typename", (Object) "dropdown-description");
            put("title", (Object) "title");
            put("description", (Object) "description");
        }});




//        result.add(new HashMap<>() {{
//            put("id", (Object) 4);
//            put("global_parent_id", (Object) 4);
//            put("parent_id", (Object) null);
//            put("typename", (Object) "dropdown-header");
//            put("title", (Object) "Название");
//            put("description", (Object) "description");
//        }});
//
//        result.add(new HashMap<>() {{
//            put("id", (Object) 5);
//            put("global_parent_id", (Object) 4);
//            put("parent_id", (Object) 4);
//            put("typename", (Object) "dropdown-title");
//            put("title", (Object) "title");
//            put("description", (Object) "description");
//        }});
//
//        result.add(new HashMap<>() {{
//            put("id", (Object) 6);
//            put("global_parent_id", (Object) 4);
//            put("parent_id", (Object) 5);
//            put("typename", (Object) "dropdown-description");
//            put("title", (Object) "title");
//            put("description", (Object) "description");
//        }});


        return result;
    }

    @GetMapping(value = "/help/statistic/globalIds")
    public List<Integer> getGlobalIds() {
        List<Integer> result = new ArrayList<>() {{ add(1);  }};
        return result;
    }

}