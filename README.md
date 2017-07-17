<img  src="https://github.com/YukiMatsumura/denbun/blob/master/art/logo.png?raw=true" align="right" />

# Denbun

[![Download](https://api.bintray.com/packages/yuki312/maven/denbun/images/download.svg)](https://bintray.com/yuki312/maven/denbun/_latestVersion)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![codecov](https://codecov.io/gh/YukiMatsumura/denbun/branch/master/graph/badge.svg)](https://codecov.io/gh/YukiMatsumura/denbun)
[![CircleCI](https://circleci.com/gh/YukiMatsumura/denbun.svg?style=shield)](https://circleci.com/gh/YukiMatsumura/denbun)

Many applications are display messages using Dialogs, Toasts and Snackbars.  
However, that messages may seem intrusive and tired.  

Denbun is a lightweight library.  
Denbun("電文") in Japanese is called "Message" in English.  
This library supports to suppress messages and adjust frequency.  

For example...

 - Dialog with "Don't ask again"
 - One shot (or N shots) dialog
 - Showing once per week
 - Dialog for light users

Denbun records the display time and counts.  
And it helps to calculate the best timing of next display.  


## Usage

Following code will record the message state.  

```
Denbun msg = DenbunPool.find(ID)
msg.shown(() -> dialog.show())
```

You can adjust the frequency using this state.

```
Denbun msg = DenbunPool.find(ID,
    state -> state.count == 0 ? Frequency.MIN : Frequency.MAX);
if (msg.isShowable()) {
  msg.shown();  // state.count was increment.
}
```

Or suppress messages.

```
Denbun msg = DenbunPool.take(ID)
msg.suppress(true);  // msg.isShowable() is returned false.
```


### Preset frequency adjusters

There are several adjusters provided in this library.

Adjuster Name    | Description
:----------------|:-------------
CountAdjuster    | For N shots dialogs
IntervalAdjuster | For periodic dialogs
CoolDownAdjuster | For periodic and N shots dialogs


## Testability

You can mock/spy the Denbun data access.

```
DenbunConfig conf = new DenbunConfig(app);

// spy original DaoProvider
Dao.Provider originalDaoProvider = conf.daoProvider();
conf.daoProvider(pref -> (spyDao = spy(originalDaoProvider.create(pref))));
DenbunPool.init(conf);

DenbunPool.find("id").shown();
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
