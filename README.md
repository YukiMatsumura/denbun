# Denbun &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; [![Download](https://api.bintray.com/packages/yuki312/maven/denbun/images/download.svg)](https://bintray.com/yuki312/maven/denbun/_latestVersion) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![codecov](https://codecov.io/gh/YukiMatsumura/denbun/branch/master/graph/badge.svg)](https://codecov.io/gh/YukiMatsumura/denbun) [![CircleCI](https://circleci.com/gh/YukiMatsumura/denbun.svg?style=shield)](https://circleci.com/gh/YukiMatsumura/denbun)

<br />

<p align="center">
<img src="https://github.com/YukiMatsumura/denbun/blob/master/art/logo.png?raw=true" width="176" height="120" alt="denbun" />
</p>

<br />

"Denbun" is a lightweight library.  
This library supports to suppress messages and adjust frequency.  

Denbun("電文") in Japanese is called "Message" in English.  
Many applications are display messages using Dialogs, Toasts and Snackbars.  
However, the message may be disturbing and may seem boring.  
So, it is important to display as necessary to the required timing.  

For example...

 - Dialog with "Don't ask again"
 - One shot (or N shots) dialog
 - Showing once per week
 - Dialog for light users

Denbun records the display time, counts and Frequency.  
It helps to calculate the best timing of next display.  


## Usage

First, initialize DenbunPool with `Application.onCreate`.  

```java
DenbunPool.init(new DenbunConfig(this));
```

Following code will record the message state. 
Message state will stored to SharedPreference.  

```java
Denbun msg = DenbunPool.find(ID);
msg.shown(() -> dialog.show());
```

Display frequency can be adjusted with the adjuster.

```java
// This message is displayed only once.
Denbun msg = DenbunPool.find(ID, new CountAdjuster(1));
assert msg.isShowable() == true;
msg.shown();
assert msg.isShowable() == false;
```

Or suppress message.  

```java
Denbun msg = DenbunPool.take(ID);
msg.suppress(true);
```


### Preset frequency adjusters

There are several adjusters provided in this library.

Adjuster Name    | Description
:----------------|:-------------
CountAdjuster    | For N shots dialogs
IntervalAdjuster | For periodic dialogs
CoolDownAdjuster | For periodic and N shots dialogs


### Or create custom adjuster.  

```java
Denbun msg = DenbunPool.find(ID,
    s -> s.count == 0 ? Frequency.MIN : Frequency.MAX);
if (msg.isShowable())
  msg.shown();  // s.count will increment.
```


## How it works?

Denbun saves the display history of the message in SharedPreference and manages it.  
You can find the default SharedPreference path in `DenbunConfig.PREF_NAME`.  

Denbun instance is pooled in DenbunPool. 
Since DenbunPool has Application scope, you can access DenbunPool from anywhere in the application and obtain Denbun instance.


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
Copyright 2017 Matsumura Yuki. Licensed under the Apache License, Version 2.0;
```
