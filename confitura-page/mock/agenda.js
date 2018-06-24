const _ = require('lodash');
const uuid = require('uuid/v4');
const entities = require('./entities');
const slots = [
  aSlot('09:00', '09:40'),
  aSlot('09:40', '10:00'),
  aSlot('10:00', '10:20')
];
const rooms = [
  aRoom('confitura', 1),
  aRoom('marmolada', 2),
  aRoom('dżem', 3),
  aRoom('marmolada', 4),
  aRoom('inne', 5),
];


module.exports = (app) => {


  app.get('/rooms', function (req, res) {
    res.send(embedded('rooms', rooms));
  });

  app.get('/time-slots', function (req, res) {
    res.send(embedded('timeSlots', slots));
  });

  app.get('/agenda', function (req, res) {
    res.send(embedded('agendaEntries',
      _.flatten(slots.map(slot =>
        rooms.map(room => anEntry(slot, room, entities.presentation()))
      ))
    ));
  });

};

function embedded(name, values) {
  return ({_embedded: {[name]: values}});
}

function aRoom(name, order) {
  return ({id: uuid(), name, order});
}

function aSlot(start, end) {
  return ({id: uuid(), start, end});
}

function anEntry(timeSlot, room, presentation) {
  const timeSlotId = timeSlot.id;
  const roomId = room.id;
  const presentationId = presentation.id;
  return {id: uuid(), timeSlotId, roomId, presentationId, presentation};
}

