const express = require('express');
const faker = require('faker');
// const Base64 = require('base64-js');
const app = express();
let id = 0;


app.use(function (req, res, next) {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept, Authorization');
  res.header('Access-Control-Allow-Methods', '*');
  next();
});
app.use((req, res, next) => {
  setTimeout(() => next(), 0);
});
require('./presentations')(app);
require('./partners')(app);

let person = function (type) {
  id += 1;
  return {
    name: faker.name.findName(),
    email: faker.internet.email(),
    admin: type % 5 === 0,
    volunteer: type % 5 === 1,
    id: `${type}_${id}`,
    photo: faker.image.avatar()
  };
};

app.get('/users/search/:type', function (req, res) {
  const type = req.params.type;
  res.send({
    _embedded: {
      users: new Array(10).fill(1).map(() => person(type))
    }
  });
});

app.get('/pages/faq', function (req, res) {
  res.send({
    id: req.params.id,
    content: `
## Registration
#### How can I buy tickets?
We will start distributing tickets around one month before the event. You can follow as on social media - we will post info there.
The tickets will be free, but you will need to make a donation to charity via our auction on charytatywni.allegro.

#### How long is c4p opened?
Until 4th of April

#### I have missed a registration process, can I attend the conference?
Sorry, because of so many attendees, it is impossible. If you are interested in Confitura talks, you can join our streaming (link soon). Feel invited also to join Warszawa JUG meetups and Confitura 2019 edition
`
  });
});
app.get('/pages/:id', function (req, res) {
  const content = `## ${req.params.id}
  ${faker.lorem.paragraphs(5)}
  `;
  res.send({
    id: req.params.id,
    content: content
  });
});


app.get('/login/twitter/callback', (req, res) => {
  const user = {
    jti: '1',
    sub: 'John Smith',
    isAdmin: true,
    isNew: false
  };
  const encodedUser = Buffer.from(JSON.stringify(user)).toString('base64');
  res.send(`header.${encodedUser}.signature`);
});


app.get('/login/twitter', (req, res) => {
  res.redirect('http://localhost:8080/login/twitter');
});

app.get('/users/:id', (req, res) => {
  res.send({
    id: req.params.id,
    name: 'John Smith',
    email: 'john@smith.com',
    bio: 'Helo Hello Helo',
    admin: true,
    presentations: [
      {id: '1', title: 'Hello, World!'}
    ]
  });
});

app.get('/users/:id/presentations', (req, res) => {
  res.send({
    _embedded: {
      presentations: [
        {id: '1', title: 'Hello, World!'},
        {id: '2', title: 'Second Presentation'},
      ]
    }
  });
});
let cospeakers = [
  {id: id++, name: faker.name.findName(), email: faker.internet.email(), photo: faker.image.avatar()},
  {id: id++, name: faker.name.findName(), email: faker.internet.email(), photo: faker.image.avatar()}
];

app.get('/presentations/:id/cospeakers', (req, res) => {
  res.send({
    _embedded: {
      users: cospeakers
    }
  });
});

app.post('/presentations/:id/cospeakers/:email', (req, res) => {
  const email = req.params.email;
  if (email.includes('wrong')) {
    res.status(400).jsonp('error');
  } else if (email.includes('not')) {
    res.status(404).jsonp('error');

  } else {
    const user = {id: id++, name: faker.name.findName(), email: email, photo: faker.image.avatar()};
    cospeakers = [...cospeakers, user];
    res.send(user);
  }

});

app.delete('/presentations/:id/cospeakers/:email', (req, res) => {
  res.send({});
});



app.get('/users', (req, res) => {
  res.send({
    _embedded: {
      users: new Array(300)
        .fill(1)
        .map((item, idx) => person(idx))

    }
  });
});


app.listen(3000, () => console.log('Example app listening on port 3000!'));

