# -*- coding: utf-8 -*-
'''
Copyright 2015 Serendio Inc.
Author - Satish Palaniappan

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
'''
#from atk import Text


from IPython.core.release import keywords
__author__ = "Satish Palaniappan, Praveen Jesudhas"


import sys,traceback,glob
sys.path.append("../")

from ThriftWrapper import PyInterface
from Sentiment.SentiHandlers.SentiMaster import SentiHandle
from Topics import Categorize

from ThriftWrapper.ttypes import *
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer
import socket
import pickle , re, collections ,os , sys , inspect , traceback
cmd_folder = os.path.realpath(os.path.abspath(os.path.split(inspect.getfile(inspect.currentframe()))[0]))
if cmd_folder not in sys.path:
    sys.path.insert(0, cmd_folder)
from Twokenize import twokenize
from Twokenize import emoticons
path = cmd_folder+"/models/"



class PyInterfaceServer:
    def __init__(self):
	self.acronyms=self.load_obj("acronymsDict")
        self.emoticons=self.load_obj("SmileyDict")
        self.answer={}
        self.log = {}
        self.S = SentiHandle()
        self.catz = Categorize.Categorize()

    def ping(self):
        print ("Ping Success !! :D")
        return
    def load_obj(self,name):
        with open( path + name + '.pkl' , 'rb') as f:
            return pickle.load(f)

    def getSentimentScore(self, obj):
       
        '''
        Arguments List:
        general -> mainText,textType = "general"
        microblogs -> mainText, textType = "microblogs"
        comments -> mainText, textType = "comments"
        reviews -> mainText, textType = "reviews", title = "" <optional>,topDomain,subDomain = "" <depends, not always optional, refer the list in config.py>
        blogs_news -> mainText< or first paragraph>, title, textType="blogs_news",lastPara = "" <optional last paragraph>,middleParas = [] <optional middle paragraphs(separate each para with newline into string)>
        '''
        try:
            S = self.S.getSentimentScore(obj.mainText,obj.textType,obj.title,obj.middleParas,obj.lastPara,obj.topDomain,obj.subDomain)
            print ("Sentiment Text : " + obj.mainText + " ||| SentimentScore[-5 to 5]: " + str(S))
            return S
        except Exception as err:
            print(traceback.format_exc())
            print(sys.exc_info()[0])
            print err

    def getTopics(self, text):
        text=text.decode('utf-8')
        print text
        cat = self.catz.getCategory(text)
        print ("Topic Text : " + text + " ||| Topic: " + cat)
        res = cat.split('|')
        return res
   
    def getKeywords(self, text):
        text=text.decode('utf-8')
        print "Keyword Text",text
        keywords = self.catz.getKeywords(text)
        return keywords 
    def ExEmo(self,text):
        emo_list=[]
        store_emo={}
    
        for word in twokenize.tokenize(text):
            if word !=" ":
                word=word.strip()
    
                try:
                    score=self.emoticons[word]
                    emo=emoticons.analyze_tweetHeavy(word)
                    #emo_list.append(word)#
                    #emo_list.append(emo)
                    #d=dict(itertools.izip_longest(*[iter(emo_list)] * 2, fillvalue=""))
                    store_emo[word]=emo
                except:
    
                    if "@" in word:
                        word="@user"
        self.answer=store_emo
        str1=str(self.answer)
        return str1
    
    def ExAcro(self,text):
        acr_list=[]
        text=text.lower()
        store_acronyms={}
        for word in twokenize.tokenize(text):
            if word!="":
                word=word.strip()
                
                try:
                    word_after=self.acronyms[word]
                    #acr_list.append(word)
                    store_acronyms[word]=word_after
                    #self.answer['EXPANDED ACRONYMS']=acr_list
                except:
                    if "@" in word:
                        word="@user"
        self.answer=store_acronyms
        str2=str(self.answer)
        return str2
         

#port = '8002'
handler = PyInterfaceServer()
processor = PyInterface.Processor(handler)
transport = TSocket.TServerSocket(port=8004)
tfactory = TTransport.TBufferedTransportFactory()
pfactory = TBinaryProtocol.TBinaryProtocolFactory()

server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)

print "Python Interface server for Sentiment, Topics and Keywords started ..."
server.serve()
