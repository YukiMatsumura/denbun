

# ⚡ Denbun


Denbun("電文") in Japanese is called "Message" in English.  
Many applications are display messages using Dialogs, Toasts and Snackbars.  
However, that messages may seem intrusive and may be tired.  

Denbun is a lightweight library.  
This library supports to suppress messages and adjust frequency.  

For example...

 - Dialog with "Don't ask again"
 - One shot (or N shots) dialog
 - Showing once per week
 - Dialog for light users

Denbun records the display date and number of displaying.  
And it helps to calculate the best timing to display next.  


## Usage

Following code will record the message state.  

```
Denbun msg = DenbunPool.take(ID)
msg.shown(() -> dialog.show())
```

You can adjust the frequency using this state.

```
Denbun msg = DenbunPool.take(ID,
    state -> state.count == 0 ? Frequency.MIN : Frequency.MAX);
if (msg.isShowable()) { ... }
```

Or suppress messages.

```
Denbun msg = DenbunPool.take(ID)
msg.suppress(true);
```
