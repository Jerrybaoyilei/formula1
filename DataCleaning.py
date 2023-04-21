import pymysql
import requests
from lxml import html
from urllib.parse import urlparse, parse_qs

class DataCleaning:

    # Constructor for the class.
    def __init__(self):
        self.setupConnection('root', 'Jerry19980206', '127.0.0.1', 'formula1')
    
    def setupConnection(self, username, password, host, database):
        self.cnx = pymysql.connect(host = host, user = username, password = password, db = database, charset = 'utf8mb4', cursorclass = pymysql.cursors.DictCursor)
    
    # Get the actual url (i.e., final redirected url) from the wikipedia url.
    # Credit: https://stackoverflow.com/questions/47537644/python-how-to-get-the-page-wikipedia-will-redirect-me-to (answer by Kathy Razmadze)
    def getActualUrl(self, url):
        response = requests.get(url, allow_redirects = True)
        doc = html.fromstring(response.content)
        newUrl = ''
        for t in doc.xpath("//link[contains(@rel, 'canonical')]"):
            newUrl = str(t.attrib['href'])
        return newUrl

    # Update the actual_constructor column in constructor table.
    def addActualUrl(self):
        cur1 = self.cnx.cursor()
        cur2 = self.cnx.cursor()
        
        cur1.execute("SELECT * FROM constructors")
        rows = cur1.fetchall()
        for row in rows:
            url = row['url']
            actualUrl = self.getActualUrl(url)
            cur2.execute("UPDATE constructors SET actual_url = %s WHERE constructor_id = %s", [actualUrl, row['constructor_id']])
        # Initially forgot to commit so the changes were not saved to the database.
        self.cnx.commit()
        cur1.close()
        cur2.close()
    
    
    # def findRepeatedConstructors(self):
    #     # List of constructors that are potentially duplicates.
    #     constructorsList = list()
    #     idList = list()
    #     wikiList = list()

    #     # Get the entire constructor table.
    #     cur = self.cnx.cursor()
    #     cur.execute ('SELECT * FROM constructors')
    #     rows = cur.fetchall()

    #     cur2 = self.cnx.cursor()
    #     # For each row, check for similar names.
    #     for row in rows:
    #         # If the constructor name is already in the list, skip it.
    #         if (row['constructor_id'] in idList):
    #             continue;
    #         else:
    #             # Find all rows that have a similar constructor name.
    #             cur2.execute('SELECT * FROM constructors WHERE name like %s', '%' + row['name'] + '%')
    #             similarRows = cur2.fetchall()
    #             # If there are more than 1 match, add the name to the list if they are not already in the list.
    #             if len(similarRows) > 1:
    #                 for similarRow in similarRows:
    #                     if similarRow['constructor_id'] not in idList:
    #                         constructorsList.append(similarRow['name'])
    #                         idList.append(similarRow['constructor_id'])
    #                         wikiList.append(similarRow['url'])
    #     print('total number of potential duplicates: ' + str(len(constructorsList)))
    #     for i in range(len(constructorsList)):
    #         print(str(idList[i]) + ' === ' + constructorsList[i] + ' === ' + wikiList[i])
    #     cur.close()
    #     cur2.close()
        
    def main(self):
        cur = self.cnx.cursor()
        cur.execute("USE formula1")
        cur.close()
        
        self.addActualUrl()

if __name__ == '__main__':
    DataCleaning().main()


        
        
            




