FROM node:18-alpine

WORKDIR /tic-tac-toe/

COPY public/ /tic-tac-toe/public
COPY src/ /tic-tac-toe/src
COPY package.json /tic-tac-toe/

RUN npm install

EXPOSE 3000

CMD ["npm", "start"]

# docker image build -t <image_name>:<tag> <path>
# docker image build -t remmo1/tic-tac-toe-react:0.0.1-SNAPSHOT .
