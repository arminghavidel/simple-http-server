package se.ox.dto;

import se.ox.helper.JsonHelper;

import java.util.HashMap;
import java.util.Map;

public class UserDto {

    private Integer id;
    private String name;

    public UserDto() {
    }

    public UserDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static UserDto fromJson(String json) {
        Map<String, String> map = JsonHelper.readJson(json);
        return new UserDto(
                JsonHelper.getInt(map, "id"),
                JsonHelper.getString(map, "name")
        );
    }

    public String toJson() {
        Map<String, String> map = new HashMap<>();
        map.put("id", id == null ? "" : id.toString());
        map.put("name", name != null ? name : "");
        return JsonHelper.toJson(map);
    }
}
