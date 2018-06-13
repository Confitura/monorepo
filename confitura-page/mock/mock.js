const express = require('express');
const faker = require('faker');
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
require('./speakers')(app);
require('./pages')(app);

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

