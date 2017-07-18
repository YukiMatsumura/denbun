# Denbun [![Download](https://api.bintray.com/packages/yuki312/maven/denbun/images/download.svg)](https://bintray.com/yuki312/maven/denbun/_latestVersion) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![codecov](https://codecov.io/gh/YukiMatsumura/denbun/branch/master/graph/badge.svg)](https://codecov.io/gh/YukiMatsumura/denbun) [![CircleCI](https://circleci.com/gh/YukiMatsumura/denbun.svg?style=shield)](https://circleci.com/gh/YukiMatsumura/denbun)

<p align="center">
<img src="https://github.com/YukiMatsumura/denbun/blob/master/art/logo.png?raw=true" width="176" height="120" alt="denbun" />
</p>

"Denbun" is a lightweight library.  
This library supports to suppress messages and adjust frequency.  

Many applications are display messages using Dialogs, Toasts and Snackbars.  
However, the message may be disturbing and may seem boring.  
Message notification, may be poor user experience.  
So, it is important to display as necessary to the required timing.  

Denbun("電文") in Japanese is called "Message" in English.  

For example...

 - Dialog with "Don't ask again"
 - One shot (or N shots) dialog
 - Showing once per week
 - Dialog for light users

Denbun records the display time, counts and Frequency.  
And it helps to calculate the best timing of next display.  


## Usage

Following code will record the message state.  
Message state will stored to SharedPreference.  

```java
Denbun msg = DenbunPool.find(ID);
msg.shown(() -> dialog.show());
```

You can adjust the frequency using this state.  

```java
Denbun msg = DenbunPool.find(ID,
    s -> s.count == 0 ? Frequency.MIN : Frequency.MAX);
if (msg.isShowable())
  msg.shown();  // s.count will increment.
```

Or suppress message.  

```java
Denbun msg = DenbunPool.take(ID);
msg.suppress(true);
assert msg.isShowable() == false;
```


### Preset frequency adjusters

There are several adjusters provided in this library.

Adjuster Name    | Description
:----------------|:-------------
CountAdjuster    | For N shots dialogs
IntervalAdjuster | For periodic dialogs
CoolDownAdjuster | For periodic and N shots dialogs


## Testability

You can mock/spy the Denbun data IO.

```java
DenbunConfig conf = new DenbunConfig(app);

// spy original DaoProvider
Dao.Provider origin = conf.daoProvider();
conf.daoProvider(pref -> (spyDao = spy(origin.create(pref))));
DenbunPool.init(conf);

DenbunPool.find(ID).shown();
verify(spyDao, times(1)).update(any());
```

## License

```
Copyright 2017 Matsumura Yuki.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
