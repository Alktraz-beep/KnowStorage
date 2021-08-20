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

#Aqui comienza reconocimiento de voz
r = sr.Recognizer()
with sr.AudioFile("prueba.wav") as source: #Conj, Det, Prep
    print("Espera..")
    audio = r.listen(source)
    try:
        texto = r.recognize_google(audio,language='es-MX')
        time.sleep(.5)
        print("Tu dijiste: {}\n".format(texto) )
    except:
        print("Yo no pude escucharte")
#Aqui finaliza codigo 
#Aqui comienza el codigo de muletillas
nlp = spacy.load('es_core_news_sm')
doc = nlp(texto)
number1=4
def main(number1):
    res=number1+2
    return str(number1)