// IReaderService.aidl
package com.reader.service;

import com.reader.service.IReaderListener;

interface IReaderService {
      int init();
      void registerListener(IReaderListener callback);
      void unregisterListener(IReaderListener callback);
      void sendData(String data);
      String version();
}