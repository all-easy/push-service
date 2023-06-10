# Push Money Bot

@PushMyMoneyBot

User guide  
[How to start](#how-to-start)  
[Commands](#commands)  
[Last features](#last-features)

Developers guide  
[Spotless](#spotless)  
[Spring](#spring)

## How to start

1. All participants need to have Telegram nicknames.
1. Create Telegram group.
1. Add @PushMyMoneyBot to the created group.
1. Add group members.
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

Now if you miss click to /push command you can continue typing @username amount as usual in replay box, bot writes down
this command.

### Currencies

Send /currency command to choose your currency. Just visual feature)

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

[Top](#push-money-bot)
