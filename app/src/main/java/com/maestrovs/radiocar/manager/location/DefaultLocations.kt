package com.maestrovs.radiocar.manager.location

import android.location.Location
import java.util.Locale

/**
 * Created by maestromaster$ on 03/03/2025$.
 */

object DefaultLocations {
    private val capitalCities = mapOf(
        "US" to Location("").apply { latitude = 38.8951; longitude = -77.0364 }, // Washinton, США
        "GB" to Location("").apply { latitude = 51.5074; longitude = -0.1278 }, // London, UK
        "DE" to Location("").apply { latitude = 52.5200; longitude = 13.4050 }, // Berlin, Німеччина
        "FR" to Location("").apply { latitude = 48.8566; longitude = 2.3522 }, // Париж, Франція
        "UA" to Location("").apply { latitude = 50.4501; longitude = 30.5234 }, // Київ, Україна
        "IT" to Location("").apply { latitude = 41.9028; longitude = 12.4964 }, // Рим, Італія
        "ES" to Location("").apply { latitude = 40.4168; longitude = -3.7038 }, // Мадрид, Іспанія
        "PT" to Location("").apply { latitude = 38.7169; longitude = -9.1399 }, // Лісабон, Португалія
        "PL" to Location("").apply { latitude = 52.2298; longitude = 21.0122 }, // Варшава, Польща
        "GR" to Location("").apply { latitude = 37.9838; longitude = 23.7275 }, // Афіни, Греція
        "IE" to Location("").apply { latitude = 53.3498; longitude = -6.2603 }, // Дублін, Ірландія
        "LT" to Location("").apply { latitude = 54.6872; longitude = 25.2797 }, // Вільнюс, Литва
        "LV" to Location("").apply { latitude = 56.9496; longitude = 24.1052 }, // Рига, Латвія
        "EE" to Location("").apply { latitude = 59.4370; longitude = 24.7536 }, // Таллінн, Естонія
        "SE" to Location("").apply { latitude = 59.3293; longitude = 18.0686 }, // Стокгольм, Швеція
        "NO" to Location("").apply { latitude = 59.9139; longitude = 10.7522 }, // Осло, Норвегія
        "FI" to Location("").apply { latitude = 60.1695; longitude = 24.9354 }, // Гельсінкі, Фінляндія
        "DK" to Location("").apply { latitude = 55.6761; longitude = 12.5683 }, // Копенгаген, Данія
        "IS" to Location("").apply { latitude = 64.1355; longitude = -21.8954 }, // Рейк'явік, Ісландія
        "TR" to Location("").apply { latitude = 39.9208; longitude = 32.8541 }, // Анкара, Туреччина
        "MX" to Location("").apply { latitude = 19.4326; longitude = -99.1332 }, // Мехіко, Мексика
        "BR" to Location("").apply { latitude = -15.8267; longitude = -47.9218 }, // Бразиліа, Бразилія
        "CA" to Location("").apply { latitude = 45.4215; longitude = -75.6972 }, // Оттава, Канада
        "AU" to Location("").apply { latitude = -35.2809; longitude = 149.1300 }, // Канберра, Австралія
        "CN" to Location("").apply { latitude = 39.9042; longitude = 116.4074 }, // Пекін, Китай
        "IN" to Location("").apply { latitude = 28.6139; longitude = 77.2090 }, // Нью-Делі, Індія
        "JP" to Location("").apply { latitude = 35.6895; longitude = 139.6917 }, // Токіо, Японія
        "KR" to Location("").apply { latitude = 37.5665; longitude = 126.9780 },  // Сеул, Південна Корея
        "RO" to Location("").apply { latitude = 44.4268; longitude = 26.1025 }, // Бухарест, Румунія
        "MD" to Location("").apply { latitude = 47.0105; longitude = 28.8638 }, // Кишинів, Молдова
        "CZ" to Location("").apply { latitude = 50.0755; longitude = 14.4378 }, // Прага, Чехія
        "SK" to Location("").apply { latitude = 48.1486; longitude = 17.1077 }, // Братислава, Словаччина
        "HU" to Location("").apply { latitude = 47.4979; longitude = 19.0402 }, // Будапешт, Угорщина
        "RS" to Location("").apply { latitude = 44.7866; longitude = 20.4489 }, // Белград, Сербія
        "HR" to Location("").apply { latitude = 45.8150; longitude = 15.9819 }, // Загреб, Хорватія
        "ME" to Location("").apply { latitude = 42.4304; longitude = 19.2594 }, // Подгориця, Чорногорія
        "AL" to Location("").apply { latitude = 41.3275; longitude = 19.8189 }  // Тирана, Албанія

    )

    fun getDefaultLocation(): Location {
        val countryCode = Locale.getDefault().country
        return capitalCities[countryCode] ?: capitalCities["US"]!! // США як fallback
    }
}
