# Noto

#### Android notes application built using kotlin.

[<img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" alt="Get it on Google Play" height="80">](https://play.google.com/store/apps/details?id=com.noto)

## Motivation

The app is a part of my [Portfolio](https://alialbaali.com) projects. It showcases my skills regarding developing Android apps.

## Screenshots

<p float="left">
<img src="Screenshots/Frame%203.png" height="400"  alt="screenshot"/>
<img src="Screenshots/Frame%206.png" height="400"  alt="screenshot"/>
<img src="Screenshots/Frame%205.png" height="400"  alt="screenshot"/>
<img src="Screenshots/Frame%204.png" height="400"  alt="screenshot"/>
<img src="Screenshots/Frame%202.png" height="400"  alt="screenshot"/>
<img src="Screenshots/Frame%201.png" height="400"  alt="screenshot"/>
<img src="Screenshots/Frame%208.png" height="400"  alt="screenshot"/>
<img src="Screenshots/Frame%207.png" height="400"  alt="screenshot"/>
</p>

## Features

* Minimal and simple design
* Group notes using libraries.
* List and grid layout modes
* Reminders
* Auto save
* Reading mode
* Each library has notes archive.
* Duplicating notes within the same library
* Moving notes to different libraries
* Coping notes to different libraries
* Notes word-count
* Starred notes
* Export notes and libraries
* Dark mode
* Support for devices with API 21 (Lollipop) and up
* No permissions are required
* All data stored locally on the device
* Ad-Free

## Architecture

The app uses Clean Architecture with MVVM design pattern and it's divided into 3 main layers.

#### Domain

Contains model, repository and data sources interfaces.

#### Data

Contains database and repository and DAO implementations.

#### Presentation (the rest of the app)

Contains all the UI logic.

## License

Noto is distributed under the terms of the Apache License (Version 2.0). See [License](LICENSE.md) for details.
