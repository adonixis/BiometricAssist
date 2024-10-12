# BiometricAssist
Решение для хакатона «Всероссийский хакатон по биометрии 2024» в кейсе «Разработка ассистента для упрощения регистрации в Единой биометрический системе»

Программное решение разработано на языке Kotlin 1.9.22 для Android версии 7.0 (API level 24) и выше.  
Android-проект содержит в себе два модуля — подключаемую библиотеку `biometricassist` и тестовое приложение `app`.  
Библиотека `biometricassist` содержит два пакета — `face` и `voice`.  

## Face
Пакет `face` реализует оценку параметров качества изображения лица с помощью открытой библиотеки **ML Kit** от **Google** и выдачу соответствующих подсказок.  
Перечень возможных подсказок:
* NO_FACES_DETECTED — Не найдено ни одного лица
* MULTIPLE_FACES_DETECTED — Больше одного лица на изображении
* RAISE_HEAD — Поднимите голову, чтобы смотреть прямо в камеру
* LOWER_HEAD — Опустите голову, чтобы смотреть прямо в камеру
* TURN_HEAD_LEFT — Поверните голову влево, чтобы смотреть прямо в камеру
* TURN_HEAD_RIGHT — Поверните голову вправо, чтобы смотреть прямо в камеру
* ROTATE_HEAD_CLOCKWISE — Поверниту голову по часовой стрелке, чтобы смотреть прямо в камеру
* ROTATE_HEAD_COUNTERCLOCKWISE — Поверниту голову против часовой стрелки, чтобы смотреть прямо в камеру
* MOVE_PHONE_CLOSER — Приблизьте телефон к лицу
* MOVE_PHONE_FARTHER — Отодвиньте телефон от лица
* FACE_FULLY_IN_FRAME — Лицо должно быть полностью в кадре
* FACE_NOT_OBSTRUCTED — Лицо должно быть полностью открыто
* NEUTRAL_EXPRESSION_REQUIRED — Выражение лица должно быть нейтральным
* OPEN_EYES — Откройте глаза
* IMPROVE_ILLUMINATION — Обеспечьте равномерное освещение лица
* REDUCE_DISTORTION — Отодвиньте телефон от лица
Настройки параметров качества, по которым даются подсказки, находятся в класса `FaceQualityParameters`.  

Примеры работы тестового приложения на хороших фотографиях:  
<img src="https://github.com/user-attachments/assets/6093b3c8-b508-429f-86ca-b454cf8b91f8" height="500">
![image](https://github.com/user-attachments/assets/6093b3c8-b508-429f-86ca-b454cf8b91f8 =250x)
![image](https://github.com/user-attachments/assets/2e01c9c0-8476-4121-850a-42798d3f95b6 =250x)  

Примеры работы тестового приложения на плохих фотографиях:  
![image](https://github.com/user-attachments/assets/56195bb3-9124-488a-a657-4562b0473b5f =250x)
![image](https://github.com/user-attachments/assets/e7a1d7f0-09c2-4811-90f3-7ba919b4083b =250x)
![image](https://github.com/user-attachments/assets/fddae9af-4e7e-45aa-8153-44da5e810ad2 =250x)
![image](https://github.com/user-attachments/assets/fbc5f748-f30a-4ae2-addf-bef4a6e4c7e2 =250x)
![image](https://github.com/user-attachments/assets/b04ea72f-fe4c-428d-a445-a77cb8a483f6 =250x)  

В приложении имеется переключатель для отображения debug-информации, полезной для разработчиков:  
![image](https://github.com/user-attachments/assets/25210318-d4d2-4c7a-a206-3a4877496dab =250x)  

## Voice
Пакет `voice` реализует оценку параметров качества записи голоса с помощью открытой библиотеки **SpeechRecognizer**, входящей в состав **Android SDK**, и выдачу соответствующих подсказок.  
Перечень возможных подсказок:
* SPEAK_LOUDER — Говорите громче или переместитесь в более тихое место
* SPEAK_QUIETLY — Говорите тише или отодвиньте телефон от лица
* SPEAK_RUSSIAN — Произнесите указанные цифры на русском языке
* MOVE_TO_QUIETER_PLACE — Переместитесь в более тихое место
* SPEAK_ONLY_NUMBERS — Произносите только указанные на экраны цифры
* Настройки параметров качества, по которым даются подсказки, находятся в класса `VoiceQualityParameters`.  

В тестовыом приложении в строке `text` отображается распознаваемый в реальном времени текст, в строке `signalNoise` — уровень шума.

Пример работы тестового приложения с корректным воспроизведением цифр:  
![image](https://github.com/user-attachments/assets/3352faa7-a8e4-47dc-9fbb-dc694555013e =250x)  

Пример работы тестового приложения с воспроизведением текста, отличного от цифр:  
![image](https://github.com/user-attachments/assets/f453c193-bd43-4f9c-aadc-f0217dd862a3 =250x)  

Пример работы тестового приложения с воспроизведением текста на другом языке:  
![image](https://github.com/user-attachments/assets/43a6bac9-6c5d-4ff5-b8cd-d3789e24227d =250x)  

## Прототип
Прототип решения находится в разделе [Releases](https://github.com/adonixis/BiometricAssist/releases/tag/1.0)  
[biometricassist.aar](https://github.com/adonixis/BiometricAssist/releases/download/1.0/biometricassist.aar) — подключаемая aar-библиотека  
[SampleApp.apk](https://github.com/adonixis/BiometricAssist/releases/download/1.0/SampleApp.apk) — тестовое приложение, использующее библиотеку  
