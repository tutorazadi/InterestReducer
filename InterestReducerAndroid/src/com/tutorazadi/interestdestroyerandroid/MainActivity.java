package com.tutorazadi.interestdestroyerandroid;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.InputMismatchException;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static double rate;
	public static double principal, principal_original;
	public static double time;
	public static double payment_amount;
	public static double extra_payment;
	public static double simple_interest;
	public static double compound_interest;
	public static double net_interest;
	public double interestSaved;
	public static double principal_paid;
	public static int payoff_years;
	public static double payoff_months;
	
	public static double[] extra_payments, minimum_payments;
	public static double interest_paid = 0.00f;
	public static double timeSaved;
	public static NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
	public static DecimalFormat df = new DecimalFormat("#.##");

	Button getInfo;
	EditText principalTxt, interestTxt, monthsTxt, extraPaymentTxt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getInfo = (Button)findViewById(R.id.getInfo);
		
		principalTxt = (EditText)findViewById(R.id.principalTxt);
		principalTxt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		
		monthsTxt = (EditText)findViewById(R.id.monthsTxt);
		monthsTxt.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		
		interestTxt = (EditText)findViewById(R.id.interestTxt);
		interestTxt.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		
		extraPaymentTxt = (EditText)findViewById(R.id.extraPaymentTxt);
		extraPaymentTxt.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		
		getInfo.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) {
        		principal = principal_original;
        		simple_interest = principal * (rate / 100) * (time / 12);
        		interest_paid = 0.00;
        		net_interest = 0.00;
        		principal_paid = 0.00;
        		payment_amount = amortize(principal, rate, time);

        		try
        		{
        			if (principalTxt.getText().length() < 4)
        			{
        				Toast.makeText(MainActivity.this, "Minimum amount for principal must be greater than $1000.", Toast.LENGTH_LONG).show();
        				return;
        			}
        			else if (monthsTxt.getText().length() < 1)
        			{
        				Toast.makeText(MainActivity.this, "Minimum number of months must be greater than 0.", Toast.LENGTH_LONG).show();
        				return;        				
        			}
        			else if (interestTxt.getText().length() < 1)
        			{
        				Toast.makeText(MainActivity.this, "Minimum interest rate must be greater than 0%.", Toast.LENGTH_LONG).show();
        				return;
        			}
        			principal_original = principal = Double.parseDouble(principalTxt.getText().toString());
        			time = Double.parseDouble(monthsTxt.getText().toString()); 
        			rate = Double.parseDouble(interestTxt.getText().toString()); 
        			extra_payment = Double.parseDouble(extraPaymentTxt.getText().toString()); 
        		}
        		catch (NumberFormatException e)
        		{
        			Toast.makeText(MainActivity.this, "You have entered invalid data.", Toast.LENGTH_LONG).show();
        		}
                calculate(v);
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) 
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void calculate(View v)
	{		
		extra_payments = new double[(int)time];
		minimum_payments = new double[(int)time];
		
		for (int i = 0; i < time; i++)
		{
			extra_payments[i] = 0.00;
			minimum_payments[i] = 0.00;
		}
		//Toast.makeText(this, "Extra Payment: " + n.format(extra_payment), Toast.LENGTH_LONG).show();
		
		simple_interest = principal * (rate / 100) * (time / 12);
		//Toast.makeText(this, "Simple interest: " + n.format(simple_interest), Toast.LENGTH_LONG).show();
		payment_amount = amortize(principal, rate, time);

		// Calculate extra payment #s.
		for (int i = 0; i < time; i++)
		{
				if (principal <= 0)
					break;
				//System.out.println("Month #: " + i);
				net_interest = principal * (1+ (rate / 100) / 12) - principal;
				if (net_interest <= 0)
					net_interest = 0;
				if ((principal + net_interest) < (payment_amount + extra_payment))
				{
					payment_amount = principal + net_interest;
					principal_paid = payment_amount - net_interest;
					principal -= principal_paid;
					extra_payments[i] = principal;
					interest_paid += net_interest;
				}
				else
				{
					principal_paid = payment_amount - net_interest  + extra_payment;
					principal -= principal_paid;
					extra_payments[i] = principal;
					interest_paid += net_interest;
				}
				payoff_years = i / 12;
				payoff_months = (double)i % 12;
		}
		timeSaved = (time - (payoff_years * 12) - payoff_months) / 12;
		interestSaved = simple_interest - interest_paid;
		
		// Reset variables...
		principal = principal_original;
		simple_interest = principal * (rate / 100) * (time / 12);
		interest_paid = 0.00;
		net_interest = 0.00;
		principal_paid = 0.00;
		payment_amount = amortize(principal, rate, time);

		// Loop for minimum payments...
		for (int i = 1; i < time+1; i++)
		{
			if (principal <= 0)
				break;
			//System.out.println("Month #: " + i);
			net_interest = principal * (1+ (rate / 100) / 12) - principal;
			if (net_interest <= 0)
				net_interest = 0;
			if ((principal + net_interest) < payment_amount)
			{
				payment_amount = principal + net_interest;
				principal_paid = payment_amount - net_interest;
				principal -= principal_paid;
				minimum_payments[i-1] = principal;
				interest_paid += net_interest;
			}
			else
			{
				principal_paid = payment_amount - net_interest;
				principal -= principal_paid;
				minimum_payments[i-1] = principal;
				interest_paid += net_interest;
			}
		}
		
		Intent goToResultsChart = new Intent(this, ExtraPaymentChartActivity.class);
		Bundle extra = new Bundle();
		extra.putDouble("TIME_SAVED", timeSaved);
		extra.putDouble("INTEREST_SAVED", interestSaved);
		extra.putDouble("TOTAL_MONTHS", time);
		extra.putDoubleArray("EXTRA_PAYMENTS", extra_payments);
		extra.putDoubleArray("MINIMUM_PAYMENTS", minimum_payments);
		extra.putString("PRINCIPAL", principalTxt.getText().toString());
		extra.putString("INTEREST_RATE",interestTxt.getText().toString());
		extra.putString("EXTRA_PAYMENT_AMOUNT", extraPaymentTxt.getText().toString());
		goToResultsChart.putExtras(extra);
		startActivity(goToResultsChart);
	}
	
	public static double amortize(double principal, double rate, double time)
	{
		// Turn X.XX% into 0.XX for decimal figure.
		rate /= 100;
		// Turn into monthly amount instead of annual.
		rate /= 12;
		return principal * ((rate * Math.pow((1 + rate), time))/(Math.pow((1+rate),time)-1));
	}
}