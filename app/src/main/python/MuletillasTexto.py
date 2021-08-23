import speech_recognition as sr
from time import sleep
from collections import defaultdict, Counter
from _collections import OrderedDict 
#from __future__ import division
import nltk
from collections import Counter
import nltk.data
import spacy
from spacy import displacy
import time

#v1
#Aqui comienza reconocimiento de voz
from os.path import dirname, join
def main(califDuracion,califVelocidad,califTemas,txt):
   
#Aqui comienza el codigo de muletillas
    texto=str(txt)
    nlp = spacy.load('es_core_news_sm')
    doc = nlp(texto)
    conteo_DET= [token.orth_ for token in doc if token.pos_ == 'DET'] ###CCONJ, VERB, ADP, NOUN
    conteo_ADP= [token.orth_ for token in doc if token.pos_ == 'ADP']
    conteo_CCONJ= [token.orth_ for token in doc if token.pos_ == 'CCONJ']
    Conteo_DV = Counter(conteo_DET) + Counter(conteo_ADP) + Counter(conteo_CCONJ)

#Sacar el promedio de muletillas
    sum=0
    for k,v in Conteo_DV.items():
            if v >=3 :
                print('Tú repetiste=',"(",k,")",v,'veces por lo tanto' )
                sum = sum + v
    print(sum)
    if sum ==0:
        califMule=100.00
    elif sum >0 and sum<=3:
        califMule=90.00
    elif sum >3 and sum<=6:
        califMule=80.00
    elif sum >6 and sum<=9:
        califMule=70.00
    elif sum >9 and sum<=12:
        califMule=60.00
    elif sum >12 and sum<=15:
        califMule=50.00
    elif sum >15 and sum<=18:
        califMule=40.00
    elif sum >18 and sum<=21:
        califMule=30.00
    elif sum >21 and sum<=24:
        califMule=20.00
    elif sum >24 and sum<=27:
        califMule=10.00
    elif sum >27:
        califMule=0.00

    #print("porc=", califMule)
#Aqui Finaliza codigo Muletillas

# Aqui comienza calculo del promedio total

    porcV=califVelocidad*.10
    porcD=califDuracion *.10
    porcTe=califTemas * .35
    # porcCon=califCon *
    # porcTon=califTon *
    porcM = califMule *.15 # sacando procentaje de Muletillas con respecto a porcentaje Muletillas comparado con un 15%
    porcTotal=porcV+porcD+porcTe+porcM
    return "Promedio velocidad: "+str(califVelocidad)+"\n"+"Promedio duración: " +str(califDuracion)+"\n"+"Promedio temas: "+str(califTemas)+"\n"+"Promedio muletillas: "+str(califMule)+"\n"+"Promedio total: "+str(porcTotal)


