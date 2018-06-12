const faker = require('faker');

module.exports = (app) => {

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

};
