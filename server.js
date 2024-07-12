const express = require("express");
const app = express();
// This is your test secret API key.
const stripe = require("stripe")('sk_test_51PbO2lKfpy6OFgrdn1wzG78eFrtLppXiMWi3AtNKyYbdZAkaIecn3ATFiTr1mMNApXt7akduUgxcDjuopOg1qOof00XmE6INPm');

app.use(express.static("public"));
app.use(express.json());

const calculateOrderAmount = (items) => {
  // Replace this constant with a calculation of the order's amount
  // Calculate the order total on the server to prevent
  // people from directly manipulating the amount on the client
  console.log(items[0].amount)
  return items[0].amount;
};

app.post("/create-payment-intent", async (req, res) => {
  const { items } = req.body;
  const { currency } = req.body;

  // Create a PaymentIntent with the order amount and currency
  const paymentIntent = await stripe.paymentIntents.create({
    amount: calculateOrderAmount(items),
    currency: currency,
    // In the latest version of the API, specifying the `automatic_payment_methods` parameter is optional because Stripe enables its functionality by default.
    automatic_payment_methods: {
      enabled: true,
    },
  });

  res.send({
    clientSecret: paymentIntent.client_secret,
  });
});

app.get("/greet", async (req, res) => {
	res.send("hello its workking");
});



app.listen(4242, () => console.log("Node server listening on port 4242!"));