// IReaderService.aidl
package com.reader.service;

// Declare any non-default types here with import statements

interface IReaderListener {
      void onRecvData(String data);
}