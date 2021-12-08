package com.yde.sapiensdelivery.controllers.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yde.sapiensdelivery.R;
import com.yde.sapiensdelivery.controllers.delivery_man.OrderStatusDeliveryManActivity;
import com.yde.sapiensdelivery.entities.Customer;
import com.yde.sapiensdelivery.entities.Order;
import com.yde.sapiensdelivery.entities.ShoppingList;
import com.yde.sapiensdelivery.gateways.OrderGateway;
import com.yde.sapiensdelivery.gateways.database.OnDataReadListener;
import com.yde.sapiensdelivery.use_cases.CustomerManager;
import com.yde.sapiensdelivery.use_cases.DeliveryManManager;
import com.yde.sapiensdelivery.use_cases.OrderManager;
import com.yde.sapiensdelivery.use_cases.ShoppingListManager;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderCompletionActivity extends AppCompatActivity {
    private OrderManager orderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_completion);

        FloatingActionButton floatingActionButton = findViewById(R.id.rating_next_BT);
        TextView totalTV = findViewById(R.id.customer_total_TV);
        ListView shoppingLists = findViewById(R.id.customer_o_status_lv);

        CustomerManager customerManager = new CustomerManager((Customer)
                getIntent().getSerializableExtra("CUSTOMER"));

        OrderGateway orderGateway = new OrderGateway();
        orderGateway.get(customerManager.getUsername(), new OnDataReadListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess() {
                if(getSavedObject() != null) {

                    ArrayList<Object> doubleData =  (ArrayList<Object>) getSavedObject();
                    orderManager = new OrderManager((Order) doubleData.get(0));

                    // Get the ShoppingLists and convert to String
                    ArrayList<ShoppingList> shopLists = orderManager.getShoppingLists();

                    ArrayList<String> slStrings = new ArrayList<>();
                    for (ShoppingList shoppingList : shopLists) {
                        ShoppingListManager shoppingListManager = new ShoppingListManager(shoppingList);
                        slStrings.add(shoppingListManager.displayEntire());
                    }

                    // Set ListView adapter
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            OrderCompletionActivity.this, android.R.layout.simple_list_item_1, slStrings);
                    shoppingLists.setAdapter(adapter);

                    totalTV.setText("Total: $ " + orderManager.getTotalPrice());
                    }
                }

            @Override
            public void onFailure() {
            }
        });



        floatingActionButton.setOnClickListener(view -> {
            // TODO remove Order from DB (pass the DeliveryMan to be rated ?)

            DeliveryManManager deliveryManManager = new DeliveryManManager(orderManager.getDeliveryMan());

            Intent intent = new Intent( OrderCompletionActivity.this, CustomerRatingActivity.class);
            deliveryManManager.passValue(intent);
            customerManager.passValue(intent);
            startActivity(intent);
        });
    }
}