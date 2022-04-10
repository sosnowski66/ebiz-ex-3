package com.example

class Data {

    val categories = listOf("Laptopy", "Komputery", "Smartfony", "TV", "Odkurzacze")

    val products = mapOf<String, List<String>>(
        "Laptopy" to listOf("Macbook Pro", "Lenovo Legion", "Microsoft Surface", "Lenovo Thinkbook"),
        "Komputery" to listOf("Mac Pro", "Lenovo Legion", "HP Pavilon"),
        "Smartfony" to listOf("Iphone 13", "Galaxy S21", "Redmi Note 10", "OnePlus 9"),
        "TV" to listOf("Samsung", "LG", "Sony", "Pioner", "Sharp"),
        "Odkurzacze" to listOf("Samsung", "Zelmer", "Amica", "Siemens")
    )
}