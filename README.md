# DeepFakeDetection
 This app utilises the model that has been trained on the Celebrity-DF dataset to identify deepfake videos. The model was trained using the references from this [repository ](https://github.com/abhijitjadhav1998/Deepfake_detection_using_deep_learning) . The app serves its purpose by providing you with a media picker on its boot screen . The media picker will allow you to choose one video at a timee which can be previewd at the Home Screen itself . When the button "Predict" is clicked with a video selected ,the video is sent to the python prediction script being run in a server where the video is processed and the output is generated and further recieved on the Client's Side. The output consists of two important data fields one being the straight and simple "Real Or Fake" Judgement String and the other being the estimate of the accuracy of the judgement being referred to as "confidence".

App Preview : 

![Screenshot_20240731-071940](https://github.com/user-attachments/assets/8c144de5-f029-4be2-aef5-c652520b31bc)
![image](https://github.com/user-attachments/assets/4f542e6d-2fe7-4530-b4a6-66bae9df0d10)
