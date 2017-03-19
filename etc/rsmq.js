var RedisSMQ = require('rsmq');

var rsmq = new RedisSMQ();

var ms = rsmq._formatZeroPad(123, 4);
var ts = Number("456" + ms.toString(10).slice(0, 3));

console.log(ms);
console.log(ts)

rsmq.createQueue({qname: "myqueue"}, function(err, resp) {
  if (resp === 1) {
      console.log("queue created");
  }
});

setInterval(function() {
  console.log('Receiving message...');
  rsmq.receiveMessage({qname: "myqueue"}, function (err, resp) {
      if (resp.id) {
          console.log("Message received.", resp)
      }
      else {
          console.log("No messages for me...")
      }
  });
}, 500);

setInterval(function() {
  console.log('Sending message...');
  rsmq.sendMessage({qname: "myqueue", message: "Hello JavaScript World"}, function (err, resp) {
      if (resp) {
          console.log("Message sent. ID: ", resp);
      }
  });
}, 2000);
