import socket
import cPickle as cp
import numpy as np
from gensim.models import KeyedVectors
import argparse
import sys

a_value = 'a2'
p_value = 'p2000'

nn_model_file = open('model_a2_p2000.pickle', 'r')
nn_model = cp.load(nn_model_file)

# Load Node2Vec Trained Model
node2vec_model = KeyedVectors.load_word2vec_format('./emd/supShort_supShort.emd')


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('--count', default='20')
    sys.stdout.flush()
    return parser.parse_args()


def getSimilarCases(data):
    data = data.split(" ")
    outputCount = int(parse_args().count)
    input = []

    for val in data:
        input.append(float(val))
    # print input
    inputArr = []
    inputArr.append(input)

    lvInput = np.array(inputArr)
    predictions = nn_model.predict(lvInput)
    # print(predictions[0])

    word2vec_model_output = node2vec_model.similar_by_vector(predictions[0], topn=outputCount)
    # create a list of the documents only, returned by the model. Remove the vector values
    modelReturnedDocumentList = []
    for i in range(0, len(word2vec_model_output)):
        modelReturnedDocumentList.append(int(str(word2vec_model_output[i][0])))

    return "data=" + str(modelReturnedDocumentList)[1:-1]


HOST = "localhost"
PORT = 8888
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print('Server created')

try:
    s.bind((HOST, PORT))
except socket.error as err:
    print('Bind failed. Error Code : '.format(err))

s.listen(10)
print("Server Listening")
conn, addr = s.accept()

while (True):
    data = conn.recv(20240)
    # print(data.decode())
    conn.send(bytes(getSimilarCases(data) + "\r\n"))
