

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

### Simple to use

```
Denbun msg = DenbunPool.get(ID)

if (isShowable())
  msg.shown()
```

This will record the display history.
You can adjust the frequency using this records.

```
adj = (history) -> {
  return history.count == 0 ? Frequency.LOW : Frequency.HIGH;
}

DenbunPool.get(ID).frequencyAdjuster(adj)
  .isShowable() ...
```

