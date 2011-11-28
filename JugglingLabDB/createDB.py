#!/usr/bin/env python
# -*- coding: utf-8 -*-
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

"""
createDB - Create a sqlite3 DataBase for the Juggling Lab application on Android.

@author: Romain RICHARD
@contact: romain.richard.IT.engineer@gmail.com
@version: 0.1
@copyright: GPLv3
"""

import itertools
import sqlite3
import sys


class createDB:
    """Create a DataBase and add data to it."""

    def __init__(self):
        self.conn = sqlite3.connect("./BDD.db")
        self.cursor = self.conn.cursor()

    def close(self):
        """Commit the changes and close the connection to the DB."""
        self.conn.commit()
        self.cursor.close()

    def exec_query(self, query):
        """Execute a query.

        @type query: String
        @param query: The query

        """
        print("query = {query}".format(query=query))
        self.cursor.execute(query)

    def create_table(self, name, columns):
        """Create a table.

        @type name: String
        @param name: The name of the table

        @type columns: Dictionary
        @param columns: The columns of the table (name: type)

        """
        query = "CREATE TABLE {name} ({columns});".format(name=name,
                                                          columns=", ".join(["{k} {v}".format(k=k, v=v) for k, v in columns]))
        self.exec_query(query)

    def insert(self, table, values):
        """Insert values into a table.

        @type table: String
        @param table: The name of the table

        @type values: Dictionary
        @param values: The values to insert (column name: value)

        """
        query = "INSERT INTO {table} ({columns}) VALUES ('{values}');".format(table=table,
                                                                              columns=", ".join(values.keys()),
                                                                              values="', '".join(str(v) for v in values.values()))
        self.exec_query(query)

    def select(self, columns, tables, where={}):
        """Select values from tables.

        @type columns: List
        @param columns: The columns names to select

        @type tables: List
        @param tables: The tables to select the columns from

        @type where: Dictionary
        @param where: Conditions to select (column name: value)

        """
        query = "SELECT {columns} FROM {tables} {where};".format(columns=", ".join(columns),
                                                                tables=", ".join(tables),
                                                                where=("WHERE {cond}".format(cond=" AND ".join(["{k}='{v}'".format(k=k, v=v) for k, v in where.items()])) if where else ""))
        self.exec_query(query)

    def insert_link(self, table1, where1, table2, where2, link_table, link_values={}):
        """Insert a link between two tables in another one.

        @type table1: String
        @param table1: The name of the first table

        @type where1: Dictionary
        @param where1: Conditions to select for table1 (column name: value)

        @type table2: String
        @param table2: The name of the second table

        @type where2: Dictionary
        @param where2: Conditions to select for table2 (column name: value)

        @type link_table: String
        @param link_table: The name of the table to insert the link into

        @type link_values: Dictionary
        @param link_values: The values to insert (column name: value)

        """
        id_table1_name = "ID_{table}".format(table=table1.upper())
        self.select([id_table1_name], [table1], where1)
        id_table1_value = self.cursor.fetchone()[0]

        id_table2_name = "ID_{table}".format(table=table2.upper())
        self.select([id_table2_name], [table2], where2)
        id_table2_value = self.cursor.fetchone()[0]

        link_values.update({id_table1_name: id_table1_value, id_table2_name: id_table2_value})
        self.insert(link_table, link_values)


def main():
    db = createDB()

    ############################################################################
    ################################## TABLES ##################################
    ############################################################################
    db.create_table("Prop",
                    [("ID_PROP", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                     ("CODE", "TEXT NOT NULL"),
                     ("XML_LINE_NUMBER", "INTEGER NOT NULL")]
                   )

    db.create_table("Hands",
                    [("ID_HANDS", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                     ("CODE", "TEXT NOT NULL"),
                     ("XML_LINE_NUMBER", "INTEGER"),
                     ("CUSTOM_DISPLAY", "TEXT")]
                   )

    db.create_table("Body",
                    [("ID_BODY", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                     ("CODE", "TEXT NOT NULL"),
                     ("XML_LINE_NUMBER", "INTEGER"),
                     ("CUSTOM_DISPLAY", "TEXT")]
                   )

    db.create_table("Trick",
                    [("ID_TRICK", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                     ("PATTERN", "TEXT NOT NULL"),
                     ("ID_PROP", "INTEGER NOT NULL"),
                     ("ID_HANDS", "INTEGER NOT NULL"),
                     ("ID_BODY", "INTEGER NOT NULL"),
                     ("XML_DISPLAY_LINE_NUMBER", "INTEGER"),
                     ("XML_DESCRIPTION_LINE_NUMBER", "INTEGER"),
                     ("CUSTOM_DISPLAY", "TEXT"),
                     ("CUSTOM_DESCRITPION", "TEXT"),
                     ("FOREIGN KEY (ID_PROP)", "REFERENCES Prop(ID_PROP)"),
                     ("FOREIGN KEY (ID_HANDS)", "REFERENCES Hands(ID_HANDS)"),
                     ("FOREIGN KEY (ID_BODY)", "REFERENCES Body(ID_BODY)")]
                   )

    db.create_table("Spin",
                    [("ID_SPIN", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                     ("SPIN_X", "INTEGER"),
                     ("SPIN_Y", "INTEGER"),
                     ("SPIN_Z", "INTEGER")]
                   )

    db.create_table("TrickSpin",
                    [("ID_TRICK", "INTEGER NOT NULL"),
                     ("ID_SPIN", "INTEGER NOT NULL"),
                     ("THROW", "INTEGER NOT NULL"),
                     ("IS_POSITION", "INTEGER NOT NULL"),
                     ("FOREIGN KEY (ID_TRICK)", "REFERENCES Trick(ID_TRICK)"),
                     ("FOREIGN KEY (ID_SPIN)", "REFERENCES Spin(ID_SPIN)"),
                     ("PRIMARY KEY (ID_TRICK, ID_SPIN)", "")]
                   )

    db.create_table("Collection",
                    [("ID_COLLECTION", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                     ("XML_LINE_NUMBER", "INTEGER"),
                     ("CUSTOM_DISPLAY", "TEXT")]
                   )

    db.create_table("TrickCollection",
                    [("ID_TRICK", "INTEGER NOT NULL"),
                     ("ID_COLLECTION", "INTEGER NOT NULL"),
                     ("FOREIGN KEY (ID_TRICK)", "REFERENCES Trick(ID_TRICK)"),
                     ("FOREIGN KEY (ID_COLLECTION)", "REFERENCES Collection(ID_COLLECTION)"),
                     ("PRIMARY KEY (ID_TRICK, ID_COLLECTION)", "")]
                   )

    db.create_table("TrickTutorial",
                    [("ID_TRICK", "INTEGER NOT NULL"),
                     ("ID_COLLECTION", "INTEGER NOT NULL"),
                     ("STEP", "INTEGER NOT NULL"),
                     ("GOAL", "INTEGER NOT NULL"),
                     ("FOREIGN KEY (ID_TRICK)", "REFERENCES Trick(ID_TRICK)"),
                     ("FOREIGN KEY (ID_COLLECTION)", "REFERENCES Collection(ID_COLLECTION)"),
                     ("PRIMARY KEY (ID_TRICK, ID_COLLECTION)", "")]
                   )

    db.create_table("Catch",
                    [("ID_CATCH", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                     ("ID_TRICK", "INTEGER NOT NULL"),
                     ("DATE", "TEXT"),
                     ("CATCHS", "INTEGER"),
                     ("FOREIGN KEY (ID_TRICK)", "REFERENCES Trick(ID_TRICK)")]
                   )

    db.create_table("Goal",
                    [("ID_GOAL", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                     ("ID_TRICK", "INTEGER NOT NULL"),
                     ("DATE_BEGIN", "TEXT"),
                     ("DATE_END", "TEXT"),
                     ("GOAL", "INTEGER"),
                     ("FOREIGN KEY (ID_TRICK)", "REFERENCES Trick(ID_TRICK)")]
                   )

    db.create_table("Juggler",
                    [("ID_JUGGLER", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                     ("COLOR_SKIN", "TEXT"),
                     ("COLOR_HAIR", "TEXT"),
                     ("COLOR_EYES", "TEXT"),
                     ("COLOR_SHIRT", "TEXT"),
                     ("COLOR_PANTS", "TEXT"),
                     ("HAT", "INTEGER"),
                     ("COLOR_HAT", "TEXT"),
                     ("GLASSES", "INTEGER"),
                     ("COLOR_GLASSES", "TEXT"),
                     ("BEARD", "INTEGER"),
                     ("IS_MALE", "INTEGER"),
                     ("HEIGHT", "INTEGER"),
                     ("WEIGHT", "INTEGER")]
                   )

    ############################################################################
    ################################# ANDROID ##################################
    ############################################################################
    db.create_table("android_metadata", [("locale", "TEXT DEFAULT 'en_US'")])
    db.insert("android_metadata", {"locale": "en_US"})

    ############################################################################
    ################################### PROP ###################################
    ############################################################################
    prop = ["ball", "ring", "club", "cube", "image"]
    for i in range(len(prop)):
        db.insert("Prop", {"CODE": prop[i], "XML_LINE_NUMBER": i})

    ############################################################################
    ################################## HANDS ###################################
    ############################################################################
    hands = ["",
             "(10)(32.5).",
             "(32.5)(10).",
             "(32.5)(10).(10)(32.5).",
             "(-30)(2.5).(30)(-2.5).(-30)(0).",
             "(-10,0,-50)(20,0,0).",
             "(25,0,-50)(0,0,0).", "(0,0,0)(25,0,-50).",
             ""]
    for i in range(len(hands)):
        db.insert("Hands", {"CODE": hands[i], "XML_LINE_NUMBER": i})
    # TODO: Add new hand movements

    ############################################################################
    ################################### BODY ###################################
    ############################################################################
    body = ["",
            "<(90).|(270,-125).|(90,125).|(270,-250).|(90,250).|(270,-375).>",
            "<(90).|(270,-150,50).|(270,-150,-50).|(270,-150,150).|(270,-150,-150).>",
            "<(270).|(90,-50).|(0,-25,25).|(180,-25,-25).>",
            "<(0).|(0,100).|(0,-100).|(0,200).|(0,-200).|(0,300).>",
            "(0,75,0)...(90,0,75)...(180,-75,0)...(270,0,-75)...",
            ""]
    for i in range(len(body)):
        db.insert("Body", {"CODE": body[i], "XML_LINE_NUMBER": i})

    ############################################################################
    ################################## TRICK ###################################
    ############################################################################
    trick = [
             # 3-Cascade Step By Step
             {"pattern": "300"},
             {"pattern": "33022"},
             {"pattern": "330"},
             {"pattern": "[32]3322"},
             {"pattern": "3"},
             # 4-Foutain Step By Step
             {"pattern": "40"},
             {"pattern": "(4,4)"},
             {"pattern": "4"},
             # 5-Cascade Step By Step
             {"pattern": "50500"},
             {"pattern": "52512"},
             {"pattern": "50505"},
             {"pattern": "55500"},
             {"pattern": "[52][52]55022[22][22]"},
             {"pattern": "55550"},
             {"pattern": "552"},
             {"pattern": "5551"},
             {"pattern": "5"},
             # 3-Cascade Tricks
             {"pattern": "333355500"}
#             {"pattern": "(4x,2)(2,4x)", "hands": },
#             {"pattern": "(4x,2)(2,4x)", "hands": },
#             {"pattern": "33333423", "hands": },
#             {"pattern": "3", "hands": },
#             {"pattern": "3", "hands": },
#             {"pattern": "3", "hands": },
#             {"pattern": "3", "hands": },
#             {"pattern": "3", "hands": },
#             {"pattern": "3", "hands": },
#             {"pattern": "3", "hands": },
#             {"pattern": "3", "hands": },
#             {"pattern": "3", "hands": },
#             {"pattern": "3", "hands": }
             # TODO: Add the remaining tricks
             ]
    for i in range(len(trick)):
        db.insert("Trick", {"PATTERN": trick[i]["pattern"],
                            "ID_PROP": (trick[i]["prop"] if "prop" in trick[i] else 1),
                            "ID_HANDS": (trick[i]["hands"] if "hands" in trick[i] else 1),
                            "ID_BODY": (trick[i]["body"] if "body" in trick[i] else 1),
                            "XML_DISPLAY_LINE_NUMBER": i})

    ############################################################################
    ################################ COLLECTION ################################
    ############################################################################
    collection = [
                  # Tutorials
                  "3-Cascade Step By Step",
                  "4-Fountain Step By Step",
                  "5-Cascade Step By Step",
                  # Pattern List
                  "3-Cascade Tricks"
                  # TODO: Add the remaining collections
                  ]
    for i in range(len(collection)):
        db.insert("Collection", {"XML_LINE_NUMBER": i})

    ############################################################################
    ############################## TRICKTUTORIAL ###############################
    ############################################################################
    three_cascade_step_by_step = [{"link_values": {"GOAL": 30}} for i in range(5)]
    four_fountain_step_by_step = [{"link_values": {"GOAL": 40}} for i in range(3)]
    five_cascade_step_by_step = [{"link_values": {"GOAL": 50}} for i in range(9)]
    tricktutorial = [three_cascade_step_by_step, four_fountain_step_by_step, five_cascade_step_by_step]
    len_tricktutorial = len(tricktutorial)
    for i in range(len(tricktutorial)):
        for j in range(len(tricktutorial[i])):
            tricktutorial[i][j]["where"] = {"XML_LINE_NUMBER": i}
            tricktutorial[i][j]["link_table"] = "TrickTutorial"
            tricktutorial[i][j]["link_values"]["STEP"] = j + 1

    ############################################################################
    ############################# TRICKCOLLECTION ##############################
    ############################################################################
    three_cascade_tricks = [{} for i in range(1)]  # TODO: Change 1 to 14 when the tricks will be in the DB
    # TODO: Add the remaining links between the tricks and the collections
    trickcollection = [three_cascade_tricks]
    for i in range(len(trickcollection)):
        for j in range(len(trickcollection[i])):
            trickcollection[i][j]["where"] = {"XML_LINE_NUMBER": len_tricktutorial + i}
            trickcollection[i][j]["link_table"] = "TrickCollection"

    ############################################################################
    ######################### TRICKTUTORIALCOLLECTION ##########################
    ############################################################################
    tricktutorialcollection = tricktutorial + trickcollection
    tricktutorialcollection = list(itertools.chain.from_iterable(tricktutorialcollection))
    for i in range(len(tricktutorialcollection)):
        db.insert_link("Trick",
                       {"XML_DISPLAY_LINE_NUMBER": i},
                       "Collection",
                       tricktutorialcollection[i]["where"],
                       tricktutorialcollection[i]["link_table"],
                       tricktutorialcollection[i]["link_values"] if "link_values" in tricktutorialcollection[i] else {}
                      )

    db.close()
    sys.exit(0)

if __name__ == "__main__":
    main()
