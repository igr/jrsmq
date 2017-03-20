var RedisSMQ = require('rsmq');
var rsmq = new RedisSMQ();

console.log("Create queue...");

rsmq.createQueue({qname:"myqueue"}, function (err, resp) {
    if (resp===1) {
        console.log("queue created")
        console.log("Send messages...");
        rsmq.sendMessage({qname:"myqueue", message:"Hello World"}, function (err, resp) {
            if (resp) {
                console.log("Message sent. ID:", resp);
                rsmq.sendMessage({qname:"myqueue", message:"Hello World2"}, function (err, resp) {
                    if (resp) {
                        console.log("Message sent. ID:", resp);
                        console.log("Receive messages...");

                        rsmq.getQueueAttributes({qname:'myqueue'}, function(err, resp){
                            console.log(resp);
                        });

                        /*
                        rsmq.receiveMessage({qname: "myqueue"}, function (err, resp) {
                            console.log(resp)
                            rsmq.receiveMessage({qname: "myqueue"}, function (err, resp) {
                                console.log(resp)
                            });
                        });
                        */

                    }
                });
            }
        });
    }
});


/*setInterval(function() {
  console.log('Receiving message...');
  rsmq.receiveMessage({qname: "myqueue"}, function (err, resp) {
      if (resp.id) {
          console.log("Message received.", resp)
      }
      else {
          console.log("No messages for me...")
      }
  });
}, 1000);
   */
