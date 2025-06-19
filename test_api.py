import urllib.request
import json

try:
    with urllib.request.urlopen('http://localhost:8080/api/regions/tree') as response:
        print('Status:', response.getcode())
        print('Content-Type:', response.headers.get('content-type'))
        
        if response.getcode() == 200:
            data = json.loads(response.read().decode('utf-8'))
        print('Data type:', type(data))
        if isinstance(data, dict):
            print('Keys:', list(data.keys()))
            if 'data' in data:
                print('data field type:', type(data['data']))
                if isinstance(data['data'], list) and len(data['data']) > 0:
                    print('First item keys:', list(data['data'][0].keys()))
                    print('First item regionName:', data['data'][0].get('regionName'))
        elif isinstance(data, list) and len(data) > 0:
            print('First item keys:', list(data[0].keys()))
            print('First item regionName:', data[0].get('regionName'))
        
            print('First 300 chars:', str(data)[:300])
        else:
            print('Error response code:', response.getcode())
except Exception as e:
    print('Error:', e)