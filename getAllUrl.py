'''
getAllUrl：获取用户下的所有回答
author：MorningStar
time：2017/3/25
'''
import requests
import re
import os
import json
import urllib.request
import sys
import pymysql

from bs4 import BeautifulSoup
from urllib import parse, request, error

'''
调试用
f = open('F:\\Python\\log.txt','w')
sys.stdout = f
'''


#常量MAXSIZE文件大小不能超过2G
MAXFSIZE = 2147483648
filesize = 0

users = set("")
count1=0
Total =0
arrlist = []
    
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
建立用户的用户名文件夹
'''
def mkdir(path):
    isExists = os.path.exists(path)
    if not isExists:
        os.makedirs(path)
        return True
    else:
        return False
'''
更改filename名称来传入数据库，以防出错
'''
def changeFilename(filename):
    
    for c in filename:
        if c == '\\':
            filename = filename.replace(c,'/')
    
    return filename

'''
列表中的每一个条目为数据库中每一个条目
每一个条目实为一个元组
'''

def insertIntoDatabase():
    connect = pymysql.Connect(
        host='localhost',
        port=3306,
        user='root',
        passwd='147258zeng',
        db='search_engine',
        charset='utf8'
    )
    #get Cursor
    cursor = connect.cursor()

    global arrlist
    
    try:
        sql = "INSERT INTO user_ans_info (username,file_address,updated_time,answer_url) VALUES ( %s, %s,%s,%s )"
        cursor.executemany(sql,arrlist)
        connect.commit()
        print('成功插入', cursor.rowcount, '条数据')
        arrlist = []
    except Exception as e:
        connect.rollback()
        print('事务处理失败',e)
        sys.exit(0)
    
    cursor.close()
    connect.close()
    

    

def isMax():
    if(filesize<MAXFSIZE):
        return False
    return True
'''
通过用户来获取他所有的回答
userUrl：用户页面的URL
'''
def getAllAnswer(userName,userUrl,url_token):
    global arrlist
    if(isMax()):
        return 
    limit = 20
    offset = 0
    former_url='https://www.zhihu.com/api/v4/members/'+url_token+'/answers'
    
    #print(former_url)
    headers = getHeaders()
    headers['Referer'] = userUrl

    #print(headers['Referer'])
    data = {
        'include':'data[*].is_normal,suggest_edit,comment_count,collapsed_counts,reviewing_comments_count,can_comment,content,voteup_count,reshipment_settings,comment_permission,mark_infos,created_time,updated_time,relationship.is_authorized,voting,is_author,is_thanked,is_nothelp,upvoted_followees;data[*].author.badge[?(type=best_answerer)].topics',
        'limit':limit,
        'offset': offset,
        'sort_by': "created"
    }
    path = "F:\\ZhihuUser\\"+userName

    if(mkdir(path)):
        print('success')
    else:
        print('failed')
    
    cnt = 0
    page = 1
    while True:
        headers['Referer'] = userUrl+'?page='+str(page)
        
        postdata = urllib.parse.urlencode(data).encode('utf-8')
        url = former_url
        page+=1
        #print(url)
        r = session.get(url,headers=headers,params=postdata)
        try:
            dic = r.json()
            isout = str(dic['paging']['is_end'])
            #print(isout)
            
           
            

            offset = offset+20
            data['offset']=offset

            for i in dic['data']:
                question_title = i['question']['title']
                
                #sys.exit(0)
                #print(question_title)
                try:
                    #print('buildFile')
                    buildFile(i,question_title,path,userName)
                    '''
                    如果这时候有2000条数据则插入进数据库
                    
                    '''
                    if len(arrlist)%2000 == 0 and len(arrlist) !=0:
                        insertIntoDatabase()

                    if(isMax()):

                        return 
                except IOError as e:
                    print(e.reason)

            if isout =='True':
                break
        except Exception as err:
            print('former_url：'+former_url)
            print("username:"+str(userName))
            print('postdata：'+str(postdata))
            print(err)
            break
            
        
        
    #print("success")
'''
利用正则表达式检查URL是否正确

'''
def checkUrl(answer_url):
    p = r'https://www.zhihu.com/question/[0-9]+/answer/[0-9]+'
    pattern = re.compile(p)
    match = re.search(pattern,answer_url)
    
    if match.group(0) is not answer_url:
        return False
    else:
        return True


'''
获得该用户在某一问题下回答的URL
'''
def getAnswerUrl(slice):

    questionID = slice['question']['url'][38:]
    if(questionID == ''):
        print('error : questionID is empty')
    answerID = slice['url'][36:]
    if(answerID == ''):
        print('error : answerID is empty')
    
    answer_url = 'https://www.zhihu.com/question/'+questionID+'/answer/'+answerID
    if checkUrl(answer_url) is  False:
        print("something wrong about the answer_url")
        sys.exit(0)
    return answer_url
    

'''
创建user下回答文件
'''   
def buildFile(slice,title,path,username):
   
    
    global arrlist
    try:
        
        title = title[0:-1]
        title = replace(title)
        
        filename = path+"\\"+title+".txt"
        updated_time = slice['question']['updated_time']
        
        answer_url = getAnswerUrl(slice)
        
        sqldata=(username,filename,updated_time,answer_url)
        files = open(filename,'wb') 
        content=slice['content'].encode('utf-8')
        soup = BeautifulSoup(content,'lxml')
        files.write(soup.text.encode('utf-8'))
        files.close()
        
        arrlist.append(sqldata)
        
        global filesize
        filesize +=getFileSize(filename)
    except IOError as err:
        print("IO error: {0}".format(err))
        
    #filesize +=getFileSize(path+"\\"+title+" --"+username+"的回答.txt") 

def getFileSize(path):
    try:
        size = os.path.getsize(path)
        return size
    except Exception as err:
        print(err)
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

def deepSearch(username,deepth,former_url_token):
    
    #print("count:"+str(count1))
    #print("进入递归第"+str(deepth)+"层")
    if(deepth>3):
        return

    if(isMax()):
        return 
    limit = 1
    offset = 0
    
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
        try:
            dic = r.json()
        
            isout = str(dic['paging']['is_end'])
            if isout =='True':
                break



            offset =offset+1
            data['offset']=offset
            for i in dic['data']:
                uName = i['name']
                
                #print('father_node:'+str(father_token))
                url_token = i['url_token']
                if url_token in users:
                    continue
                users.add(url_token)
                #print('url_token:'+str(url_token))
                try:
                    getAllAnswer(uName,getReferer(url_token),url_token)
                    if(isMax()):
                        return 
                    deepSearch(uName,deepth+1,url_token)
                except Exception as err:
                    print(err)
                    

            isout = str(dic['paging']['is_end'])
            if isout =='True':
                break
        except Exception as err:
            print(err)
            break
        
    
def replace(s):
    
    s="".join(s.split('\n'))
    s="".join(s.split('\r'))
    s="".join(s.split('\t'))
    format = "\\.\"[]:{}><!@#$%^&*()=_+“”。，？?；：‘’、|/"
    for c in s:
        if c in format:
            s = s.replace(c,'、')
    return s

if __name__ == '__main__':
     
     session = requests.Session()
     headers = getHeaders()
     
     html_doc = session.get('https://www.zhihu.com/org/lens-27/answers',headers=headers)
     file = open('F:\\python\\spider\\tem.html','wb')
     file.write(html_doc.text.encode('utf-8'))
     file.close()
     userName = '壹间的生活家'
     userUrl = 'https://www.zhihu.com/people/room1969/answers'
     url_token = 'room1969'
     users.add(url_token)
     getAllAnswer(userName,userUrl,url_token)
     '''
     深度遍历最多20层， 最多15g文档
     '''
     deepth=1
     print("进入递归")
     deepSearch(userName,deepth,url_token)
     insertIntoDatabase()
     