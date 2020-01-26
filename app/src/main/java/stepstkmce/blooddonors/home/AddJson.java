package stepstkmce.blooddonors.home;

public class AddJson {
    //TO NOTE
    // USE ONLY FOR UPLOADING DATA


    //new Datahandler().execute(myUrl);
    // CALL THIS TO ACTIVATE


    /*public class Datahandler extends AsyncTask<String,Void,Integer> {

        public List<Item> items=new ArrayList<>();
        int v=0;

        @Override
        protected Integer doInBackground(String... strings) {
            Integer integer=0;
            HttpURLConnection connection;
            URL url;
            try {
                url=new URL(strings[0]);
                connection=(HttpURLConnection) url.openConnection();
                int statusCode=connection.getResponseCode();
                if (statusCode==200) {
                    String line;
                    BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder=new StringBuilder();
                    while ((line=reader.readLine())!=null) {
                        stringBuilder.append(line).append('\n');
                    }
                    parseResult(stringBuilder.toString());
                    integer=1;
                }else {
                    integer=0;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return integer;
        }

        private void parseResult(String string) {
            try {
                JSONArray init_Array=new JSONArray(string);
                for (int i=0;i<init_Array.length();i++) {
                    JSONObject item_obj=init_Array.getJSONObject(i);
                    Item item=new Item();
                    if (item_obj.getString("PLACE").equals("") || item_obj.getString("BLOOD GROUP").equals("") || item_obj.getString("PHONE NUMBER").equals("")
                            || item_obj.getString("NAME").equals("")) {

                    }else {
                        item.setAdm(item_obj.getString("ADM NO").toUpperCase());
                        item.setBranch(item_obj.getString("BRANCH").toUpperCase());
                        item.setGroup(item_obj.getString("BLOOD GROUP").toUpperCase());
                        item.setPlace(item_obj.getString("PLACE").toUpperCase());
                        item.setYear(item_obj.getString("YEAR"));
                        item.setNumber(item_obj.getString("PHONE NUMBER"));
                        item.setName(item_obj.getString("NAME").toUpperCase());
                        item.setDonated(false);
                        item.setLast(null);
                        items.add(item);
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer==1) {
                Toast.makeText(getApplicationContext(), "h"+items.size(), Toast.LENGTH_SHORT).show();
                for (Item item: items) {
                    firestore.collection("Information").add(items)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (!task.isSuccessful()) {
                                        try {
                                            Toast.makeText(getApplicationContext(), "Cannot complete", Toast.LENGTH_SHORT).show();
                                            v++;
                                        }catch (Exception e) {

                                        }

                                    }
                                }
                            });

                }
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "failed to process :"+v, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Process Failed", Toast.LENGTH_SHORT).show();
            }
        }

    }*/
}
