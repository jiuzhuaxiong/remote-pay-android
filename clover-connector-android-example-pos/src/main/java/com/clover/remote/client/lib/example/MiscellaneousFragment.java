/*
 * Copyright (C) 2016 Clover Network, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.clover.remote.client.lib.example;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import com.clover.remote.client.CloverConnector;
import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.lib.example.model.OrderObserver;
import com.clover.remote.client.lib.example.model.POSDiscount;
import com.clover.remote.client.lib.example.model.POSExchange;
import com.clover.remote.client.lib.example.model.POSLineItem;
import com.clover.remote.client.lib.example.model.POSOrder;
import com.clover.remote.client.lib.example.model.POSPayment;
import com.clover.remote.client.lib.example.model.POSRefund;
import com.clover.remote.client.lib.example.model.POSStore;

import java.lang.ref.WeakReference;

public class MiscellaneousFragment extends Fragment {
  private static final String ARG_STORE = "store";

  private POSStore store;

  boolean updatingSwitches = false;

  private OnFragmentInteractionListener mListener;

  private WeakReference<ICloverConnector> cloverConnectorWeakReference;
  private Switch manualSwitch;
  private Switch swipeSwitch;
  private Switch chipSwitch;
  private Switch contactlessSwitch;
  private RadioGroup allowOfflineRG;
  private RadioGroup approveOfflineNoPromptRG;
  private Switch printingSwitch;

  public static MiscellaneousFragment newInstance(POSStore store, ICloverConnector cloverConnector) {
    MiscellaneousFragment fragment = new MiscellaneousFragment();
    fragment.setStore(store);
    Bundle args = new Bundle();
    fragment.setCloverConnector(cloverConnector);
    fragment.setArguments(args);

    store.addCurrentOrderObserver(new OrderObserver() {
      @Override public void lineItemAdded(POSOrder posOrder, POSLineItem lineItem) {

      }

      @Override public void lineItemRemoved(POSOrder posOrder, POSLineItem lineItem) {

      }

      @Override public void lineItemChanged(POSOrder posOrder, POSLineItem lineItem) {

      }

      @Override public void paymentAdded(POSOrder posOrder, POSPayment payment) {

      }

      @Override public void refundAdded(POSOrder posOrder, POSRefund refund) {

      }

      @Override public void paymentChanged(POSOrder posOrder, POSExchange pay) {

      }

      @Override public void discountAdded(POSOrder posOrder, POSDiscount discount) {

      }

      @Override public void discountChanged(POSOrder posOrder, POSDiscount discount) {

      }
    });

    return fragment;
  }

  public MiscellaneousFragment() {
    // Required empty public constructor
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_miscellaneous, container, false);

    manualSwitch = ((Switch)view.findViewById(R.id.ManualSwitch));
    swipeSwitch = ((Switch)view.findViewById(R.id.SwipeSwitch));
    chipSwitch = ((Switch)view.findViewById(R.id.ChipSwitch));
    contactlessSwitch = ((Switch)view.findViewById(R.id.ContactlessSwitch));
    allowOfflineRG = (RadioGroup) view.findViewById(R.id.AcceptOfflinePaymentRG);
    approveOfflineNoPromptRG = (RadioGroup) view.findViewById(R.id.ApproveOfflineWithoutPromptRG);
    printingSwitch = ((Switch) view.findViewById(R.id.PrintingSwitch));

    manualSwitch.setTag(CloverConnector.CARD_ENTRY_METHOD_MANUAL);
    swipeSwitch.setTag(CloverConnector.CARD_ENTRY_METHOD_MAG_STRIPE);
    chipSwitch.setTag(CloverConnector.CARD_ENTRY_METHOD_ICC_CONTACT);
    contactlessSwitch.setTag(CloverConnector.CARD_ENTRY_METHOD_NFC_CONTACTLESS);

    RadioGroup.OnCheckedChangeListener radioGroupChangeListener = new RadioGroup.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(!updatingSwitches) {
          CloverConnector cc = (CloverConnector) cloverConnectorWeakReference.get();
          if(cc == null) {
            Log.e(getClass().getSimpleName(), "Clover Connector reference is null");
            return;
          }
          if (group == allowOfflineRG) {
            int checkedRadioButtonId = group.getCheckedRadioButtonId();
            Boolean allowOffline = null;
            switch (checkedRadioButtonId) {
              case R.id.acceptOfflineDefault :  { allowOffline = null; break; }
              case R.id.acceptOfflineFalse : { allowOffline = false; break; }
              case R.id.acceptOfflineTrue : { allowOffline = true; break; }
            }
            store.setAllowOfflinePayment(allowOffline);
          } else if (group == approveOfflineNoPromptRG) {
            int checkedRadioButtonId = group.getCheckedRadioButtonId();
            Boolean approveWOPrompt = null;
            switch (checkedRadioButtonId) {
              case R.id.approveOfflineWithoutPromptDefault:  { approveWOPrompt = null; break; }
              case R.id.approveOfflineWithoutPromptFalse: { approveWOPrompt = false; break; }
              case R.id.approveOfflineWithoutPromptTrue: { approveWOPrompt = true; break; }
            }
            store.setApproveOfflinePaymentWithoutPrompt(approveWOPrompt);
          }
        }
      }
    };

    CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!updatingSwitches) {
          store.setCardEntryMethods(getCardEntryMethodStates());
        }
      }
    };
    updateSwitches(view);

    manualSwitch.setOnCheckedChangeListener(changeListener);
    swipeSwitch.setOnCheckedChangeListener(changeListener);
    chipSwitch.setOnCheckedChangeListener(changeListener);
    contactlessSwitch.setOnCheckedChangeListener(changeListener);

    allowOfflineRG.setOnCheckedChangeListener(radioGroupChangeListener);
    approveOfflineNoPromptRG.setOnCheckedChangeListener(radioGroupChangeListener);

    printingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!updatingSwitches) {
          store.setDisablePrinting(isChecked);
        }
      }
    });
    return view;
  }

  public void onButtonPressed(Uri uri) {
    if (mListener != null) {
      mListener.onFragmentInteraction(uri);
    }
  }

  private int getCardEntryMethodStates() {
    int val = 0;
    val |= manualSwitch.isChecked() ? (Integer)manualSwitch.getTag() : 0;
    val |= swipeSwitch.isChecked() ? (Integer)swipeSwitch.getTag() : 0;
    val |= chipSwitch.isChecked() ? (Integer)chipSwitch.getTag() : 0;
    val |= contactlessSwitch.isChecked() ? (Integer)contactlessSwitch.getTag() : 0;

    return val;
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mListener = (OnFragmentInteractionListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
    }
  }

  @Override public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  public void setStore(POSStore store) {
    this.store = store;
  }

  public interface OnFragmentInteractionListener {
    public void onFragmentInteraction(Uri uri);
  }

  public void setCloverConnector(ICloverConnector cloverConnector) {
    cloverConnectorWeakReference = new WeakReference<ICloverConnector>(cloverConnector);
  }

  private void updateSwitches(View view) {
    if (manualSwitch != null) {

      updatingSwitches = true;
      CloverConnector cc = (CloverConnector)cloverConnectorWeakReference.get();
      if(cc == null) {
        Log.e(getClass().getSimpleName(), "Clover Connector Weak Reference is null");
        return;
      }
      manualSwitch.setChecked((store.getCardEntryMethods() & CloverConnector.CARD_ENTRY_METHOD_MANUAL) == CloverConnector.CARD_ENTRY_METHOD_MANUAL);
      contactlessSwitch.setChecked((store.getCardEntryMethods() & CloverConnector.CARD_ENTRY_METHOD_NFC_CONTACTLESS) == CloverConnector.CARD_ENTRY_METHOD_NFC_CONTACTLESS);
      chipSwitch.setChecked((store.getCardEntryMethods() & CloverConnector.CARD_ENTRY_METHOD_ICC_CONTACT) == CloverConnector.CARD_ENTRY_METHOD_ICC_CONTACT);
      swipeSwitch.setChecked((store.getCardEntryMethods() & CloverConnector.CARD_ENTRY_METHOD_MAG_STRIPE) == CloverConnector.CARD_ENTRY_METHOD_MAG_STRIPE);

      printingSwitch.setChecked(store.getDisablePrinting() != null ? store.getDisablePrinting() : false);

      Boolean allowOfflinePayment = store.getAllowOfflinePayment();
      ((RadioButton) view.findViewById(R.id.acceptOfflineDefault)).setChecked(allowOfflinePayment == null);
      ((RadioButton) view.findViewById(R.id.acceptOfflineTrue)).setChecked(allowOfflinePayment != null && allowOfflinePayment);
      ((RadioButton) view.findViewById(R.id.acceptOfflineFalse)).setChecked(allowOfflinePayment != null && !allowOfflinePayment);
      Boolean approveOfflinePaymentWithoutPrompt = store.getApproveOfflinePaymentWithoutPrompt();
      ((RadioButton) view.findViewById(R.id.approveOfflineWithoutPromptDefault)).setChecked(approveOfflinePaymentWithoutPrompt == null);
      ((RadioButton) view.findViewById(R.id.approveOfflineWithoutPromptTrue)).setChecked(approveOfflinePaymentWithoutPrompt != null && allowOfflinePayment);
      ((RadioButton) view.findViewById(R.id.approveOfflineWithoutPromptFalse)).setChecked(approveOfflinePaymentWithoutPrompt != null && !allowOfflinePayment);

      updatingSwitches = false;
    }

  }
}
