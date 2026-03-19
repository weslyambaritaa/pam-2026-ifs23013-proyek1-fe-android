package org.delcom.pam_2026_ifs23013_proyek1_fe_android.helper

class ConstHelper {
    // Route Names
    enum class RouteNames(val path: String) {
        AuthLogin(path = "auth/login"),
        AuthRegister(path = "auth/register"),

        Home(path = "home"),

        Profile(path = "profile"),
        Todos(path = "todos"),
        TodosAdd(path = "todos/add"),
        TodosDetail(path = "todos/{todoId}"),
        TodosEdit(path = "todos/{todoId}/edit"),
        Foods(path = "foods"),
        FoodsAdd(path = "foods/add"),
        FoodsDetail(path = "foods/{foodId}"),
        FoodsEdit(path = "foods/{foodId}/edit"),
    }
}