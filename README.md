# PushMoneyBot

Hold and optimize common expenses with your friends with [PushMoneyBot](https://t.me/PushMyMoneyBot)

**User guide**<br>
[How to start](#how-to-start)<br>
[Commands](#commands)<br>
[Last features](#last-features)

**Developers guide**<br>
[Spotless](#spotless)<br>
[Spring](#spring)<br>

[License](#license)

## How to start

1. Create Telegram group.
1. Add [@PushMyMoneyBot](https://t.me/PushMyMoneyBot) to the created group.
1. Group members have to register themselves with the bot, by typing `/addme` command.
1. Use commands to create and manage expense records.

## Commands

### Register a group member within bot

```
/addme

Example:
/addme @MyTelegramNickname
```

### Create an expense record

```
/push @<username> <amountOrMathExpression> [ <percent>% <oneWordTitle> ]
/push @<username> <amountOrMathExpression> [ <oneWordTitle> <percent>% ]

Example:
/push @john 111+111 30% bread
/push @john 111+111 bread 30%
/push @john 111+111 bread
/push @john 111+111 30%
/push @john 111+111
/push @john 111
```

### Setup group’s currency

```
/currency
```

Then click button with currency you want to set up.

### Display money balance

```
/result
```

### Show transaction history

```
/history [ <amountOfTransactions> ]

Example:
/history
/history 5
```

### Getting help

```
/help
```

## Last Features

### Force replays

Now if you miss click to `/push` command you can continue typing `@username amount` as usual in replay box, bot writes down
this command.

### Currencies

Send `/currency` command to choose your currency. Just visual feature)

[Top](#pushmoneybot)

## Developers Guide

### Spotless

Run Spotless plugin

```bash
# Check
./gradlew spotlessCheck

# Apply changes
./gradlew spotlessApply
```

### Spring

Run Spring Boot

```bash
# Set environment variables, then
./gradlew bootRun
```

[Top](#pushmoneybot)

## License

Attribution-NonCommercial-ShareAlike 4.0 International (CC BY-NC-SA 4.0)

You are free for non-commercial purposes to:

- Share — copy and redistribute the material in any medium or format
- Adapt — remix, transform, and build upon the material

The licensor cannot revoke these freedoms as long as you follow the license terms.

Under the following terms:

- Attribution — You must give appropriate credit, provide a link to the license, and indicate if changes were made. You
may do so in any reasonable manner, but not in any way that suggests the licensor endorses you or your use.
- NonCommercial — You may not use the material for commercial purposes.
- ShareAlike — If you remix, transform, or build upon the material, you must distribute your contributions under the
same license as the original.

Read and accept full license text in LICENSE.txt. Make sure that you also have read and accepted the following official
Creative Commons references:

- License legal text https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode
- Licence overview https://creativecommons.org/licenses/by-nc-sa/4.0/

### Attribution

By All-Easy ©, Attribution-NonCommercial-ShareAlike 4.0 International (CC BY-NC-SA 4.0), https://all-easy.github.io/bot

[Top](#pushmoneybot)
