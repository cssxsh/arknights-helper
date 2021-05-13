# Arknights Helper
> 基于 [Mirai Console](https://github.com/mamoe/mirai-console) 的 [明日方舟](https://ak.hypergryph.com/) 助手插件

## 指令
注意: 使用前请确保可以 [在聊天环境执行指令](https://github.com/project-mirai/chat-command)  
带括号的`/`前缀是可选的  
`<...>`中的是指令名，由空格隔开表示或，选择其中任一名称都可执行例如`/抽卡 十连`  
`[...]`表示参数，当`[...]`后面带`?`时表示参数可选  
`{...}`表示连续的多个参数

### 助手抽卡指令

| 指令                                              | 描述                                                           |
|:--------------------------------------------------|:---------------------------------------------------------------|
| `/<gacha 抽卡> <one 单抽> [times]?`               | 单抽`times`次，默认为1                                         |
| `/<gacha 抽卡> <one 十连> [times]?`               | 十连`times`次，默认为1                                         |
| `/<gacha 抽卡> <detail 详情>`                     | 查看卡池规则                                                   |
| `/<gacha 抽卡> <set 设置> [name]`                 | 设置卡池为`name`, 默认为`NORMAL`                               |
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

| 指令                                    | 描述                               |
|:----------------------------------------|:-----------------------------------|
| `/<mine 挖矿 答题> {types}?`            | 机器人会提出一个问题               |
| `/<question 问题> <detail 详情> [name]` | 查看自定义问题的详情               |
| `/<question 问题> <list 列表>`          | 列出已经设置的自定义问题           |
| `/<question 问题> <delete 删除>`        | 删除指定问题                       |
| `/<question 问题> <add 添加>`           | 与机器人互动，输入条件，设置新问题 |

回复选项序号`A~Z`，即算回答问题
`types`是提问问题类型，默认为全部类型
多个问题存在时，作答会按先出先答的原则提交答案  
快速回答(规定时间的1/3内)会有相应奖励  
群聊模式的其他群员亦可回答题目，但题目只能被回答一次，且其他人作为抢答者有相应奖励和惩罚  
题目结构如下
```
[类型](得分) 问题
A. 选项
B. 选项
...
```

### 助手公招指令

| 指令                      | 描述                        |
|:--------------------------|:----------------------------|
| `/<recruit 公招> {words}` | 查看关键词`words`的公招干员 |

`works`的数量为1~5

### 助手材料指令

| 指令                                    | 描述                         |
|:----------------------------------------|:-----------------------------|
| `/<item 材料> [name] [limit]?`           | 查看材料的关卡掉落率         |
| `/<stage 关卡> [name] [limit]?`          | 查看关卡的材料掉落率         |
| `/<zone 章节 活动 地图> [name] [limit]?` | 查看地图所有关卡的材料掉落率 |

`limit`是显示单元的最大数量

### 助手玩家指令

| 指令                                                     | 描述                            |
|:---------------------------------------------------------|:--------------------------------|
| `/<player 玩家> <detail 详情>`                           | 当前玩家(QQ号)在助手中的数据    |
| `/<player 玩家> <reason 理智> [init]`                    | 设置玩家(QQ号)当前理智为`init`  |
| `/<player 玩家> <level 等级> [index]`                    | 设置玩家(QQ号)当前等级为`index` |
| `/<player 玩家> <recruit 公招> [site] [hours] [minutes]` | 设置公招位`site`等待时间        |

玩家的默认等级为`120`  
理智的提醒时间会根据玩家等级和当前理智值`init`设置

### 助手蹲饼指令

| 指令                          | 描述                            |
|:------------------------------|:--------------------------------|
| `/<guard 蹲饼> <detail 详情>` | 当前蹲饼的状态和轮询时间        |
| `/<guard 蹲饼> <speed 速度>`  | 设置蹲饼轮询时间是`speed`分钟   |
| `/<guard 蹲饼> <open 打开>`   | 开启蹲饼状态(为当前聊天环境)    |
| `/<guard 蹲饼> <close 关闭>`  | 关闭蹲饼状态(为当前聊天环境)    |

玩家的默认等级为`120`  
理智的提醒时间会根据玩家等级和当前理智值`init`设置

### 助手数据指令

| 指令                                     | 描述         |
|:-----------------------------------------|:-------------|
| `/<data 数据> <arknights 方舟>`          | 更新游戏数据 |
| `/<data 数据> <penguin 企鹅 掉落>`       | 更新掉落数据 |
| `/<data 数据> <name alias 别称 别名>`    | 查看掉落别名 |

## 安装

### 手动安装

1. 运行 [Mirai Console](https://github.com/mamoe/mirai-console) 生成plugins文件夹
1. 从 [Releases](https://github.com/cssxsh/arknights-helper/releases) 下载`jar`并将其放入`plugins`文件夹中

## TODO
- [ ] 配置问题类型出现的概率
- [ ] 明日方舟官方QQ表情，下载、发送更新提醒等
- [ ] 自定义材料掉落别名(目前由企鹅物流数据提供支持)  
- [ ] 关卡规划
- [ ] 剿灭和周常提醒

## 数据来源
* 游戏数据 [Kengxxiao/ArknightsGameData](https://github.com/Kengxxiao/ArknightsGameData)
* 掉落数据 [企鹅物流数据统计](https://penguin-stats.io/)
* 其他数据 相关API