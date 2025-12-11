import React, {useState, useEffect} from 'react';
import {
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
  NativeModules,
  Alert,
  PermissionsAndroid,
  StatusBar,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

const {VolumeModule} = NativeModules;

function App(): React.JSX.Element {
  const [phoneNumber, setPhoneNumber] = useState('');
  const [overlayPermission, setOverlayPermission] = useState(true);

  const checkPermissions = async () => {
    try {
      // Check Call Permission
      const callGranted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.CALL_PHONE,
        {
          title: 'Phone Call Permission',
          message: 'This app needs access to make phone calls.',
          buttonNeutral: 'Ask Me Later',
          buttonNegative: 'Cancel',
          buttonPositive: 'OK',
        },
      );
      if (callGranted !== PermissionsAndroid.RESULTS.GRANTED) {
         Alert.alert('Permission Denied', 'Call permission is required.');
      }

      // Check Overlay Permission
      const hasOverlay = await VolumeModule.checkOverlayPermission();
      setOverlayPermission(hasOverlay);
      
    } catch (err) {
      console.warn(err);
    }
  };

  useEffect(() => {
    checkPermissions();
    VolumeModule.getPhoneNumber()
      .then((number: string) => {
        if (number) {
          setPhoneNumber(number);
        }
      })
      .catch((err: any) => console.error('Failed to load number:', err));
  }, []);

  const saveNumber = () => {
    if (phoneNumber.length < 3) {
      Alert.alert('Invalid Number', 'Please enter a valid phone number.');
      return;
    }
    VolumeModule.savePhoneNumber(phoneNumber);
    Alert.alert('Success', 'Phone number saved!');
  };

  const openSettings = () => {
    VolumeModule.openAccessibilitySettings();
  };

  const requestOverlay = () => {
    VolumeModule.requestOverlayPermission();
  };

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="dark-content" />
      <View style={styles.content}>
        <Text style={styles.title}>QuickCall Setup</Text>
        
        <TextInput
          style={styles.input}
          placeholder="Enter phone number"
          keyboardType="phone-pad"
          value={phoneNumber}
          onChangeText={setPhoneNumber}
        />

        <TouchableOpacity style={styles.button} onPress={saveNumber}>
          <Text style={styles.buttonText}>Save Number</Text>
        </TouchableOpacity>

        {!overlayPermission && (
          <TouchableOpacity style={[styles.button, {backgroundColor: '#FF9500'}]} onPress={requestOverlay}>
            <Text style={styles.buttonText}>Grant Overlay Permission</Text>
          </TouchableOpacity>
        )}

        <View style={styles.divider} />

        <TouchableOpacity style={[styles.button, styles.secondaryButton]} onPress={openSettings}>
          <Text style={styles.buttonText}>Enable Service</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  content: {
    padding: 24,
    justifyContent: 'center',
    flex: 1,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 32,
    color: '#333',
    textAlign: 'center',
  },
  label: {
    fontSize: 16,
    fontWeight: '600',
    marginBottom: 8,
    color: '#555',
  },
  input: {
    backgroundColor: '#fff',
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#ddd',
    padding: 16,
    fontSize: 18,
    marginBottom: 16,
  },
  button: {
    backgroundColor: '#007AFF',
    padding: 16,
    borderRadius: 8,
    alignItems: 'center',
    marginBottom: 16,
  },
  secondaryButton: {
    backgroundColor: '#4CD964',
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
  divider: {
    height: 1,
    backgroundColor: '#ddd',
    marginVertical: 24,
  },
  instruction: {
    fontSize: 14,
    color: '#666',
    textAlign: 'center',
    marginBottom: 16,
    lineHeight: 20,
  },
});

export default App;