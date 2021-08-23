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
import numpy as np
import matplotlib.pyplot as plt
import scipy.io.wavfile as waves



#v1
#Aqui comienza reconocimiento de voz
from os.path import dirname, join
def main2(califDuracion,califVelocidad,califTemas):
    filename = join(dirname(__file__), "Prep.wav")
    muestreo, sonido = waves.read(filename)
    # canales: monofónico o estéreo
    tamano = np.shape(sonido)
    muestras = tamano[0]
    m = len(tamano)
    canales = 1  # monofónico
    if (m>1):  # estéreo
        canales = tamano[1]
    # experimento con un canal
    if (canales>1):
        canal = 0
        uncanal = sonido[:,canal] 
    else:
        uncanal = sonido
    # rango de observación en segundos
    inicia = 1.000
    termina = 25.000
    # observación en número de muestra
    a = int(inicia*muestreo)
    b = int(termina*muestreo)
    parte = uncanal[a:b]
    # Salida # Archivo de audio.wav
    print('archivo de parte[] grabado...')
    variable=waves.write(filename, muestreo, parte)


# Aqui comienza calculo del promedio total

    porcV=califVelocidad*.10
    porcD=califDuracion *.10
    porcTe=califTemas * .35
    # porcCon=califCon *
    # porcTon=califTon *
    porcM = 5 *.15 # sacando procentaje de Muletillas con respecto a porcentaje Muletillas comparado con un 15%
    porcTotal=porcV+porcD+porcTe+porcM
    return variable


