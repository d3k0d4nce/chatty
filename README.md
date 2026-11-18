# Chatty

Android-приложение для просмотра чатов: список каналов, сообщения с текстом и изображениями, полноэкранный просмотр картинок.

## Стек

- Kotlin, Jetpack Compose, Material 3
- Retrofit + Gson
- Coil
- ViewModel + StateFlow

## Сборка

```bash
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## Структура

```text
app/src/main/java/ru/d3k0d4nce/chatty/
├── MainActivity.kt
├── data/ChatApi.kt
└── ui/
    ├── ChattyRoot.kt        # портретный и ландшафтный режимы, список чатов
    ├── MessagesScreen.kt    # сообщения, пагинация, карточки сообщений
    ├── ImageViewer.kt       # полноэкранная картинка
    ├── ChattyViewModel.kt
    └── theme/Theme.kt
```

## Поведение

- Поворот экрана не сбрасывает выбранный чат, сообщения и открытую картинку.
- В ландшафте слева список чатов, справа выбранный чат.
- Кнопка «Назад» и системный жест закрывают картинку, затем чат, затем приложение.
- Сообщения подгружаются по 20 штук кнопкой «Загрузить ещё».
- Последний открытый чат кешируется до выбора другого.
