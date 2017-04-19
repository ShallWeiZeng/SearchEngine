'''
该文件是为了支持断点续传所做的，首先获取所有的user
因为获取所有user占用不了太多空间，所以会递归5层
预计数据量达到百万以上
'''
import requests
import re
import os
import json
import urllib.request
import sys
import time

from bs4 import BeautifulSoup
from urllib import parse, request, error

#设置一个集合，防止重复的user进入表
users = set("")
arrlist = []
cnt=0
proxies = { "http": "http://119.102.101.29:808", "https": "https://119.102.101.29:808" }
#插入进数据库user表，表里只有3个字段username,user_token和user_url

'''
获得报文头
'''
def getHeaders():
    headers = {'User-Agent':'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36',
            'Host': 'www.zhihu.com',
            'authorization':'oauth c3cef7c66a1843f8b3a9e6a1e3160e20'
            }
    return headers

'''
由用户名获得ta关注人的ans的URL
'''
def getReferer(url_token):
    return "https://www.zhihu.com/people/"+url_token+"/answers"

'''
由用户名获得ta的URL
'''
def getRefererInFollowing(url_token):
    return "https://www.zhihu.com/people/"+url_token+"/following"

def getUserUrl(url_token):
    return 'https://www.zhihu.com/people/'+url_token+'/answers'

#递归实现调用
def getAllUser(deepth,former_url_token,former_url):
    
    global cnt
    global arrlist
    if(deepth>4):
        return
    limit = 20
    offset = 0

    time.sleep(2)
    former_url = 'https://www.zhihu.com/api/v4/members/'+former_url_token+'/followees'
    father_token = former_url_token
    #print(former_url)
    data={
        'include':'data[*].answer_count,articles_count,gender,follower_count,is_followed,is_following,badge[?(type=best_answerer)].topics',
        'limit':limit,
        'offset':offset
    }
    headers = getHeaders()
    while True:
        headers['Referer'] = getRefererInFollowing(former_url_token)
        postdata = urllib.parse.urlencode(data).encode('utf-8')
        url = former_url
        #print(url)

        r = session.get(url,headers=headers,params=postdata)
        
        dic = r.json()
        offset =offset+20
        data['offset']=offset
        print("cnt:"+str(cnt))
        print(dic)
        for i in dic['data']:
            
            uName = i['name']
            
            #print('father_node:'+str(father_token))
            url_token = i['url_token']
            #print("url_token:"+str(url_token))
            if url_token in users:
                continue
            users.add(url_token)

            user_url=getUserUrl(url_token)
            cnt+=1
            #sqldata=(username,url_token,user_url)
            #arrlist.append(sqldata)
            #print('url_token:'+str(url_token))
            
            getAllUser(deepth+1,url_token,user_url)
            
                

        isout = str(dic['paging']['is_end'])
        if isout =='True':
            break


if __name__ =='__main__':
    
     
     session = requests.Session()
     headers = getHeaders()
     MAX_RETRIES = 10
     adapter = requests.adapters.HTTPAdapter(max_retries=MAX_RETRIES)
     session.mount('https://', adapter)
     session.mount('http://', adapter)

     userName = '壹间的生活家'
     userUrl = 'https://www.zhihu.com/people/room1969/answers'
     url_token = 'room1969'
     users.add(url_token)
     cnt=1
     deepth=1
     #time.sleep(1)
     getAllUser(deepth+1,url_token,userUrl)
