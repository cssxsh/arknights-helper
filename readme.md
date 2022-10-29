# [Arknights Helper](https://github.com/cssxsh/arknights-helper)

> 基于 [Mirai Console](https://github.com/mamoe/mirai-console) 的 [明日方舟](https://ak.hypergryph.com/) 助手插件

[![Release](https://img.shields.io/github/v/release/cssxsh/arknights-helper)](https://github.com/cssxsh/arknights-helper/releases)
[![Downloads](https://img.shields.io/github/downloads/cssxsh/arknights-helper/total)](https://shields.io/category/downloads)
[![MiraiForum](https://img.shields.io/badge/post-on%20MiraiForum-yellow)](https://mirai.mamoe.net/topic/203)

**使用前应该查阅的相关文档或项目**  
**Arknights Helper 在2.0版本进行了重构 需要重新配置订阅 详见 方舟蹲饼**  

* [User Manual](https://github.com/mamoe/mirai/blob/dev/docs/UserManual.md)
* [Permission Command](https://github.com/mamoe/mirai/blob/dev/mirai-console/docs/BuiltInCommands.md#permissioncommand)
* [Chat Command](https://github.com/project-mirai/chat-command)

## 指令

注意: 使用前请确保可以 [在聊天环境执行指令](https://github.com/project-mirai/chat-command)  
带括号的`/`前缀是可选的  
`<...>`中的是指令名，由空格隔开表示或，选择其中任一名称都可执行例如`/抽卡 十连`  
`[...]`表示参数，当`[...]`后面带`?`时表示参数可选  
`{...}`表示连续的多个参数  

本插件指令权限ID 格式为 `xyz.cssxsh.mirai.plugin.arknights-helper:command.*`, `*` 是指令的第一指令名  
例如 `/公招 远程位 支援` 的权限ID为 `xyz.cssxsh.mirai.plugin.arknights-helper:command.ark-recruit`

### ~~助手抽卡指令~~

**抽卡指令 2.0 版本重构中 暂不可用**

| 指令                                            | 描述                                          |
|:----------------------------------------------|:--------------------------------------------|
| `/<gacha 抽卡> <one 单抽> [times]?`               | 单抽`times`次，默认为1                             |
| `/<gacha 抽卡> <one 十连> [times]?`               | 十连`times`次，默认为1                             |
| `/<gacha 抽卡> <detail 详情>`                     | 查看卡池规则                                      |
| `/<gacha 抽卡> <set 设置> [name]`                 | 设置卡池为`name`, 默认为`NORMAL`                    |
| `/<gacha 抽卡> <pool 卡池> [name] [set]? {rules}` | 设置卡池`name`的规则为`rules`，当`set`为`true`时设置为当前卡池 |

抽卡每一抽会消耗`600`合成玉，合成玉可以通过[答题](#助手答题指令)获得，通过[玩家详情](#助手玩家指令)查看

#### ArknightsGachaCommand 卡池规则`rules`参数格式

`rules`参数从第二行起，按行分割  
每行格式为规则`干员名|星级|other|...:概率`或者注释`#...`  
星级用连续的`*`表示，例如`*****`表示五星干员，需要单行设置，设置的概率不包括已设置干员名  
other表示剩余的其他干员  
概率用小数表示，全部行的概率加起来的概率要为`100%`，即`1.00`  
例子

```
/抽卡 卡池 限时寻访深悼
浊心斯卡蒂|凯尔希:0.014
赤冬:0.04
******:0.006
*****:0.04
****:0.48
other:0.42
```

### 助手答题指令

| 指令                                        | 描述                |
|:------------------------------------------|:------------------|
| `/<ark-mine 方舟挖矿 方舟答题> [type]?`           | 机器人会提出一个问题        |
| `/<ark-question 方舟问题> <detail 详情> [name]` | 查看自定义问题的详情        |
| `/<ark-question 方舟问题> <list 列表>`          | 列出已经设置的自定义问题      |
| `/<ark-question 方舟问题> <delete 删除>`        | 删除指定问题            |
| `/<ark-question 方舟问题> <add 添加>`           | 与机器人互动，输入条件，设置新问题 |
| `/<ark-question 方舟问题> <count 统计>`         | 答题情况统计            |

1.  回复选项序号`A~Z`，即算回答问题  
2.  `type`是提问问题类型，默认为全部类型  
    可选值 `BUILDING`, `PLAYER`, `TALENT`, `POSITION`, `PROFESSION`, `RARITY`, `POWER`, `ILLUST`, 
    `VOICE`, `SKILL`, `STORY`, `ENEMY`, `WEEKLY`, `MUSIC`, `OTHER`
3.  回答了当前问题才会出现下一个问题  
4.  快速回答(规定时间的1/3内)会有相应奖励  
5.  群聊模式的其他群员亦可回答题目，但题目只能被回答一次，且其他人作为抢答者有相应奖励和惩罚  
6.  题目结构如下

```
[类型](得分) 问题
A. 选项
B. 选项
...
```

### 助手公招指令

| 指令                            | 描述                |
|:------------------------------|:------------------|
| `/<ark-recruit 方舟公招> {words}` | 查看关键词`words`的公招干员 |

* `words`的数量为1~5  
  例如 `/方舟公招 远程位 支援`

### 助手材料指令

| 指令                                         | 描述             |
|:-------------------------------------------|:---------------|
| `/<ark-item 方舟材料> [name] [limit]? [now]?`  | 查看材料的关卡掉落率     |
| `/<ark-stage 方舟关卡> [name] [limit]? [now]?` | 查看关卡的材料掉落率     |
| `/<ark-zone 方舟章节> [name] [limit]? [now]?`  | 查看地图所有关卡的材料掉落率 |

* `limit` 是显示前多少项查询结果  
* `now` 是是否只显示当前开启关卡，默认为 `true`  

### ~~助手玩家指令~~

**表情指令 2.0 版本重构中 暂不可用**

| 指令                                                   | 描述                    |
|:-----------------------------------------------------|:----------------------|
| `/<player 玩家> <detail 详情>`                           | 当前玩家(QQ号)在助手中的数据      |
| `/<player 玩家> <reason 理智> [init]`                    | 设置玩家(QQ号)当前理智为`init`  |
| `/<player 玩家> <level 等级> [index]`                    | 设置玩家(QQ号)当前等级为`index` |
| `/<player 玩家> <recruit 公招> [site] [hours] [minutes]` | 设置公招位`site`等待时间       |
| `/<player 玩家> <record 记录>`                           | 列出玩家的公招记录             |

玩家的默认等级为`120`  
理智的提醒时间会根据玩家等级和当前理智值`init`设置

### 助手蹲饼指令

| 指令                                                      | 描述       |
|:--------------------------------------------------------|:---------|
| `/<ark-guard 方舟蹲饼> <detail 详情>`                         | 查看蹲饼详情   |
| `/<ark-guard 方舟蹲饼> <blog 微博> [contact] {blogs}`         | 设置微博蹲饼内容 |
| `/<ark-guard 方舟蹲饼> <video 视频> [contact] {videos}`       | 设置视频蹲饼内容 |
| `/<ark-guard 方舟蹲饼> <announce 公告> [contact] {announces}` | 设置公告蹲饼内容 |

* `contact` 为群号或Q号  
* `blogs` 可选值为 `ARKNIGHTS`(官号), `BYPRODUCT`(朝陇山), `MOUNTEN`(一拾山), `HISTORICUS`(泰拉记事社)  
  例如 `/方舟蹲饼 微博 123456 ARKNIGHTS MOUNTEN`, 就订阅了官方号和一拾山
* `video` 可选值为 `ANIME`, `MUSIC`, `GAME`, `ENTERTAINMENT`  
  例如 `/方舟蹲饼 视频 123456 GAME MUSIC`, 就订阅了PV和音乐单曲
* `announce` 可选值为 `ANDROID`, `IOS`, `BILIBILI`  
  例如 `/方舟蹲饼 公告 123456 ANDROID`, 就订阅了官服的公告 

### 助手数据指令

| 指令                            | 描述     |
|:------------------------------|:-------|
| `/<ark-data 方舟数据> <clear 清理>` | 清理缓存   |
| `/<ark-data 方舟数据> <cron 定时>`  | 重载定时设置 |

位于 `Mirai-Console` 运行目录下的 `config/xyz.cssxsh.mirai.plugin.arknights-helper` 文件夹下的 `cron.json` 文件

### 助手表情指令

** 需要 [Meme Helper](https://github.com/cssxsh/meme-helper) 作为前置**

| 指令                             | 描述       |
|:-------------------------------|:---------|
| `/<ark-face 方舟表情> <random 随机>` | 随机发送一个表情 |
| `/<ark-face 方舟表情> <detail 详情>` | 查看表情详情   |

## 配置文件

位于 `Mirai-Console` 运行目录下的 `config/xyz.cssxsh.mirai.plugin.arknights-helper` 文件夹下的 `config` 文件

## 安装

### MCL 指令安装

`./mcl --update-package xyz.cssxsh:arknights-helper --channel stable --type plugin`

### 手动安装

1. 运行 [Mirai Console](https://github.com/mamoe/mirai-console) 生成`plugins`文件夹
2. 从 [Releases](https://github.com/cssxsh/arknights-helper/releases) 下载`jar`并将其放入`plugins`文件夹中

## TODO

- [ ] 配置问题类型出现的概率
- [x] 记录答题正确率 21/06/10
- [x] 明日方舟官方QQ表情，下载、发送更新提醒等 21/05/14
- [x] 自定义材料掉落别名(目前由企鹅物流数据提供支持) 21/05/14
- [ ] 关卡规划
- [ ] 剿灭和周常提醒
- [x] 游戏公告

## 数据来源

* 游戏数据 [Kengxxiao/ArknightsGameData](https://github.com/Kengxxiao/ArknightsGameData)
* 掉落数据 [企鹅物流数据统计](https://penguin-stats.io/)
* 其他数据 相关API
