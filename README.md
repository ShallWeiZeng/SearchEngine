# SearchEngine
关于知乎的搜索引擎
## 爬虫部分<br/>
getAllUrl.py实现对逻辑上的对所有网页内容的爬取，但因为磁盘大小，和需要缓存的节点数较多，作为一般部分直接设置为不能超过4层，最后的文件总大小不得超过2G

如果出现以下错误<br/>
former_url：https://www.zhihu.com/api/v4/members/tanchaobo2/answers<br/>
username:勃失败

former_url：https://www.zhihu.com/api/v4/members/li-chang-zhu-6/answers<br/>
username:李昌竹

两种可能性：
- 代码真错了
- 该用户已被拉黑

还有一种错误<br/>
requests.exceptions.ConnectionError: HTTPSConnectionPool(host='www.zhihu.com', port=443): Max retries exceeded with url

这种错误是代理错误，服务器认为你在爬取它，知乎的反爬虫机制。这个bug不好重现，暂时未修复，不过可以在python代码添加这两行

```python
    MAX_RETRIES = 100
    adapter = requests.adapters.HTTPAdapter(max_retries=MAX_RETRIES)
    session.mount('https://', adapter)
    session.mount('http://', adapter)
```
