![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=for-the-badge)
# MNS-Android-Sample
This is the sample project created to demonstrate the usage of Alibaba Cloud Messaging and Notification Services.

## Built using

 - Android Studio 3.0.0
 - MNS SDK 1.1.8

## Overview of this application

This application shows a demo of Notification & Messaging services capability of the Alibaba Cloud Messaging and Notification Services (MNS). Please find the device screenshots in "Snapshots" section

## Prerequisites

 - You need a Alibaba Cloud Account. If you want one you can get one with **free credit of $300** by registering [here](https://account-intl.aliyun.com/register/intl_register.htm?biz_params=%7B%22intl%22%3A%22%7B%5C%22referralCode%5C%22%3A%5C%226qnq3f%5C%22%7D%22%7D)

## Installation

 1. Clone or download the project into your Android Studio 3.0.0 
 2. You need the /libs folder for proper functioning. **DON'T REMOVE LIBS FOLDER.**

### Access Keys Information
Please replace your information from your Alibaba Cloud console in "strings.xml".

```
<resources>
    <string name="app_name">YOUR APP NAME</string>

    <!-- Alibaba MNS Service details-->
    <!-- Please replace this details with your own-->
    <!--Public Endpoint-->
    <string name="Endpoint">UPDATE YOUR ENDPOINT FOR QUEUES HERE</string>
    <!-- Access ID -->
    <string name="AccessKey">UPDATE YOUR ACCESS ID</string>
    <!-- Access key Secret -->
    <string name="AccessKeySecret">UPDATE YOUR ACCESS KEY HERE</string>
    <!-- Queue Names -->
    <string name="QueueName_1">-QUEUE 1 FOR SEND AND RECEIVE MESSAGE</string>
    <string name="QueueName_2">QUEUE 2 FOR NOTIFICATIONS</string>

</resources>
```

## Bugs & Feedback
For bugs, questions and discussions please use the [Github Issues.](https://github.com/saichandu415/MNS-Android-Sample/issues)


## Snapshots
|                                          Home                                         |                                               Sending Message                                               |
|:-------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------:|
|<img src="https://github.com/saichandu415/MNS-Android-Sample/raw/master/snapshots/Home.png" width="432" height="768" /> | <img src="https://github.com/saichandu415/MNS-Android-Sample/raw/master/snapshots/Message_Sending_inProgress.png" width="432" height="768" />| 

|                                          Messages Sent                                         |                                               Receiving Messages                                              | 
|:----------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------:|
|<img src="https://github.com/saichandu415/MNS-Android-Sample/raw/master/snapshots/Messages_Sent.png" width="432" height="768" /> |<img src="https://github.com/saichandu415/MNS-Android-Sample/raw/master/snapshots/Receiving_Message_inProgress.png" width="432" height="768" />|



|                                        Messages Received                                         |                                               Sending Notification                                               |                                         Notifications Received                                         |
|:-------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------:|
| <img src="https://github.com/saichandu415/MNS-Android-Sample/raw/master/snapshots/Recevied_Message.png" width="216" height="384" /> | <img src="https://github.com/saichandu415/MNS-Android-Sample/raw/master/snapshots/Sending_Notification_inProgress.png" width="216" height="384" /> | <img src="https://github.com/saichandu415/MNS-Android-Sample/raw/master/snapshots/Notification_Received.png" width="216" height="384" /> |

## License

