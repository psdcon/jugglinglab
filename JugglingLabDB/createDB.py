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

    def find_id(self, table, where):
        """Find the ID of an item in a table.

        @type table: String
        @param table: The name of the table

        @type where: Dictionary
        @param where: Conditions to select (column name: value)

        """
        self.select(["ID_{table}".format(table=table.upper())], [table], where)
        res = self.cursor.fetchone()
        if not res:
            self.insert(table, where)
            res = [self.find_id(table, where)]
        return res[0]


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
                     ("IS_TUTORIAL", "INTEGER NOT NULL"),
                     ("XML_LINE_NUMBER", "INTEGER"),
                     ("CUSTOM_DISPLAY", "TEXT")]
                   )

    db.create_table("TrickCollection",
                    [("ID_TRICK", "INTEGER NOT NULL"),
                     ("ID_COLLECTION", "INTEGER NOT NULL"),
                     ("STEP", "INTEGER"),
                     ("GOAL", "INTEGER"),
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
             "(25)(12.5).(12.5)(25).",
             "(-30)(2.5).(30)(-2.5).(-30)(0).",
             "(-20)(25).(25)(-20).",
             "(0)(32.5).",
             "(-10,0,-50)(20,0,0).",
             "(25,0,-50)(0,0,0).",
             "(0,0,0)(25,0,-50).",
             ""]
    for i in range(len(hands)):
        db.insert("Hands", {"CODE": hands[i], "XML_LINE_NUMBER": i})

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
             {"pattern": "333355500"},
             {"pattern": "(4x,2)(2,4x)", "hands": "(-25,-15)(25,30).(17.5,45)(10,45).(17.5,45)(10,45).(-25,-15)(25,30)."},
             {"pattern": "(4x,2)(2,4x)", "hands": "(-7.5,-15)(30,50).(0,40)(0,35).(0,40)(0,35).(-7.5,-15)(30,50)."},
             {"pattern": "33333423", "hands": "(-7.5,20)(32.5).(10)(32.5).(10)(32.5).(10)(32.5).(10)(30,35).(5)(32.5).(0,35)(0,30).(-10)(32.5)."},
             {"pattern": "3", "hands": "(45,20)(52.5,45)."},
             {"pattern": "3", "hands": "(10)(35,15).(10)(20).(35,15)(20)."},
             {"pattern": "3", "hands": "(7.5,95)(30,95)."},
             {"pattern": "3", "hands": "(32.5)(10)."},
             {"pattern": "3", "hands": "(30)(-20)."},
             {"pattern": "3", "hands": "(10)(32.5).(10)(32.5).(10)(32.5).(32.5)(25).(10)(-32.5,25).(20,-10)(32.5,-15).(0,10)(32.5).(10)(32.5)."},
             {"pattern": "3", "hands": "(7.5,-10)(32.5,-15).(32.5)(25).(10)(-32.5,25)."},
             {"pattern": "3", "hands": "(10)(32.5).(10)(32.5).(10)(32.5).(32.5)(25,20).(10,-15)(-40,-15).(10,20)(32.5,15).(0)(32.5).(10)(32.5)."},
             {"pattern": "3", "hands": "(7.5,20)(32.5,15).(32.5)(25,20).(10)(-32.5,-25)."},
             {"pattern": "3", "hands": "(-30)(-10)."},
             # 3-ball Tricks
             {"pattern": "(4,0)(4,4)", "hands": "(0)(25).(25)(25).(25)(0).(25)(25)."},
             {"pattern": "(4,4)(4x,0)(4,4)(0,4x)", "hands": "(17.5)(35).(17.5)(17.5).(0)(17.5).(17.5)(17.5).(17.5)(17.5).(17.5)(35).(17.5)(17.5).(0)(17.5)."},
             {"pattern": "(4,4)(4x,0)(4,4)(0,4)(4,4)(0,4x)(4,4)(4,0)", "hands": "(17.5)(35).(17.5)(17.5).(35)(17.5).(17.5)(17.5).(17.5)(17.5).(17.5)(0).(17.5)(17.5).(0)(17.5).(17.5)(17.5).(17.5)(35).(17.5)(17.5).(35)(17.5).(17.5)(0).(17.5)(17.5).(0)(17.5).(17.5)(17.5)."},
             {"pattern": "(4,4)(4x,0)(4,4)(0,4x)", "hands": "(-15)(0).(30)(15).(0)(-15).(15)(30).(30)(15).(-15)(0).(15)(30).(0)(-15)."},
             {"pattern": "(4,4)(4,0)", "hands": "(10)(32.5).(10)(10).(-32.5)(10).(10)(10)."},
             {"pattern": "(6,6)(2x,0)(0,4x)", "hands": "(10)(32.5,35).(10)(10).(10)(10).(10)(10).(10)(10).(32.5,35)(10)."},
             {"pattern": "(2,4)", "hands": "(12.5,15)(12.5,65).(-12.5)(12.5,-5).(12.5,65)(12.5,10).(12.5)(-12.5,-5)."},
             {"pattern": "(2,4)", "hands": "(12.5)(12.5,50).(-12.5,20)(12.5,15).(12.5,50)(12.5,-5).(12.5,20)(-12.5,15)."},
             {"pattern": "(2,4)", "hands": "(0,15)(0,65).(0)(25,-5).(0,65)(0,10).(-25,-5)(0,-5)."},
             {"pattern": "(4,2)", "hands": "(25)(0,-5).(25)(25,50).(0)(25,-5).(25,50)(25,-5)."},
             {"pattern": "(4,2)", "hands": "(0)(25,-5).(25)(25,50).(25)(0,-5).(25,50)(25,-5)."},
             {"pattern": "(2,4)(2,4x)(4,2)(4x,2)", "hands": "(25)(25,50).(25)(0,-5).(25,50)(25,-5).(0)(25,-5).(25)(0,-5).(25)(25,50).(0)(25,-5).(25,50)(25,-5)."},
             {"pattern": "(4,2)", "hands": "(-10)(10).(0,20)(-25,20).(10)(-10).(0,20)(25,20)."},
             {"pattern": "441"},
             {"pattern": "3", "hands": "(50,50)(37.5,25).(25)(50,50).(37.5,25)(25).(50,50)(37.5,25).(25)(50,50).(37.5,25)(25)."},
             {"pattern": "3", "hands": "(30,20)(7.5).(22.5,95)(25,100)."},
             {"pattern": "3", "hands": "(30)(7.5,20).(22.5,95)(25,85)."},
             {"pattern": "(2x,4x)", "hands": "(30,50)(0,-25).(35)(25)."},
             {"pattern": "(2x,4)(4,2x)", "hands": "(35,50)(25,-15).(15)(25).(15)(25).(35,50)(25,-15)."},
             {"pattern": "441", "hands": "(12.5,-10)(37.5,20).(15)(12,5,-10).(22.5,50)(15)."},
             {"pattern": "(2x,4x)(4x,2)(4x,2x)(2,4x)", "hands": "(30,50)(25).(25)(15).(10)(25).(15)(15).(25)(15).(30,50)(25).(15)(15).(10)(25)."},
             {"pattern": "242334", "hands": "(25,-15)(25,-15).(25)(0).(25,65)(25,65).(0)(15).(-25,65)(12.5,20).(15)(25)."},
             {"pattern": "2334", "hands": "(25,55)(0,60).(0)(25).(-25,60)(0,55).(0)(25)."},
             {"pattern": "3", "hands": "(-17.5,60)(17.5,60).(-17.5)(17.5)."},
             {"pattern": "423", "hands": "(-25,20)(0,20).(25,50)(-25,50).(0,10)(25,-15)."},
             # 4-ball Tricks
             {"pattern": "(4,4)", "hands": "(32.5)(10)."},
             {"pattern": "4", "hands": "(32.5)(10)."},
             {"pattern": "(4x,4x)(4,4)", "hands": "(30,10)(10).(30)(10).(10)(30).(10)(30,10)."},
             {"pattern": "(4x,4x)", "hands": "(17.5,15)(32.5).(17.5)(32.5,15)."},
             {"pattern": "(4x,4x)", "hands": "(17.5,15)(32.5,15).(17.5)(32.5).(17.5)(32.5).(17.5,15)(32.5,15)."},
             {"pattern": "444447333"},
             {"pattern": "44453", "hands": "(10)(0).(10)(37.5,15).(10)(22.5).(37.5,15)(22.5).(10)(22.5)."},
             # 5-ball Tricks
             {"pattern": "5", "hands": "(32.5)(10)."},
             {"pattern": "[32][32][32][32][32][32][52][52][52]555555522"},
             {"pattern": "555555744"},
             # Shower
             {"pattern": "51"},
             {"pattern": "5223"},
             {"pattern": "3", "hands": "(37.5,40)(20).(20)(37.5,40)."},
             {"pattern": "71", "hands": "(25)(12.5).(12.5)(25)."},
             {"pattern": "53", "hands": "(25)(12.5).(12.5)(25)."},
             {"pattern": "5", "hands": "(37.5,40)(20).(20)(37.5,40)."},
             {"pattern": "73", "hands": "(25)(12.5).(12.5)(25)."},
             {"pattern": "(4x,6x)", "hands": "(12.5)(25).(25)(12.5)."},
             {"pattern": "75", "hands": "(25)(12.5).(12.5)(25)."},
             {"pattern": "(2x,4x)", "hands": "(12.5)(25).(25)(12.5)."},
             {"pattern": "(2x,6x)", "hands": "(12.5)(25).(25)(12.5)."},
             {"pattern": "7131", "hands": "(25)(12.5).(12.5)(25)."},
             {"pattern": "(2x,6x)(2x,2x)", "hands": "(15)(30,15).(30,15)(15).(15)(15,15).(15,15)(15)."},
             {"pattern": "315171", "hands": "(25)(12.5).(12.5)(25)."},
             {"pattern": "9151", "hands": "(25)(12.5).(12.5)(25)."},
             {"pattern": "(2x,4x)", "hands": "(-7.5,20)(7.5,20).(-25)(12.5)."},
             {"pattern": "(2x,4x)", "hands": "(0,20)(12.5,20).(25)(7.5).(0,20)(12.5,20).(-25)(7.5)."},
             {"pattern": "35", "hands": "(37.5,40)(20).(20)(37.5,40)."},
             {"pattern": "3", "hands": "(-17.5,35)(17.5,35).(-32.5)(32.5)."},
             {"pattern": "[97]121", "hands": "(25)(12.5).(12.5)(25)."},
             {"pattern": "3", "hands": "(-25,-10)(25).(25,10)(-25,20)."},
             # Mills Mess
             {"pattern": "330", "hands": "(-30)(2.5).(30)(-2.5).(-30)(0)."},
             {"pattern": "3", "hands": "(-30)(2.5).(30)(-2.5).(-30)(0)."},
             {"pattern": "423", "hands": "(-30)(2.5).(30)(-2.5).(-30)(0)."},
             {"pattern": "414", "hands": "(-30)(2.5).(30)(-2.5).(-30)(0)."},
             {"pattern": "315", "hands": "(-30)(2.5).(30)(-2.5).(-30)(0)."},
             {"pattern": "612", "hands": "(-30)(2.5).(30)(-2.5).(-30)(0)."},
             {"pattern": "4", "hands": "(-30)(2.5).(30)(-2.5).(-30)(0)."},
             {"pattern": "534", "hands": "(-30)(2.5).(30)(-2.5).(-30)(0)."},
             {"pattern": "552", "hands": "(-30)(2.5).(30)(-2.5).(-30)(0)."},
             {"pattern": "642", "hands": "(-30)(2.5).(30)(-2.5).(-30)(0)."},
             {"pattern": "5", "hands": "(-30)(2.5).(30)(-2.5).(-30)(0)."},
             {"pattern": "6", "hands": "(-30)(2.5).(30)(-2.5).(-30)(0)."},
             {"pattern": "864", "hands": "(-30)(2.5).(30)(-2.5).(-30)(0)."},
             {"pattern": "[34]23", "hands": "(-30)(2.5).(30)(-2.5).(-30)(0)."},
             {"pattern": "3", "hands": "(-30,5)(-5,10).(30,10)(-5,5).(-15,-20)(15,-15)."},
             {"pattern": "3", "hands": "(30)(0,10).(-30,-15)(0,-15).(-30,10)(30).(-15)(0)."},
             {"pattern": "3", "hands": "(-2.5)(-30).(0)(-30).(2.5)(30)."},
             {"pattern": "44133", "hands": "(-30,25)(0,10).(30)(25).(-7.5)(17.5,-10).(25,10)(-5).(-25,-15)(5,-10)."},
             {"pattern": "(4x,4x)(0,0)", "hands": "(-25,10)(-10).(-25,-5)(-10,-15).(30,40)(15,50).(30,40)(15,50).(-25,-5)(-10,-15).(-25,10)(-10).(30,40)(15,50).(30,40)(15,50)."},
             {"pattern": "(4x,4x)(0,4x)(4x,4x)(4x,0)", "hands": "(-25,10)(-10).(-25,-5)(-10,-15).(30,40)(15,50).(30,40)(15,50).(-25,-5)(-10,-15).(-25,10)(-10).(30,40)(15,50).(30,40)(15,50)."},
             {"pattern": "(4x,4x)", "hands": "(-25,10)(-10).(-25,-5)(-10,-15).(30,40)(15,50).(30,40)(15,50).(-25,-5)(-10,-15).(-25,10)(-10).(30,40)(15,50).(30,40)(15,50)."},
             # Box
             {"pattern": "(2x,4)(0,2x)", "hands": "(5)(25).(25)(25).(25)(25).(5)(25)."},
             {"pattern": "(2x,4)(4,2x)", "hands": "(5)(25).(25)(25).(25)(25).(5)(25)."},
             {"pattern": "126"},
             {"pattern": "630"},
             {"pattern": "(2x,4x)(2x,4)(4x,2x)(4,2x)", "hands": "(17.5)(35).(0)(0).(0)(0).(35)(35).(0)(0).(17.5)(35).(35)(35).(0)(0)."},
             {"pattern": "633", "hands": "(25)(17.5).(0)(25).(0)(17.5)."},
             {"pattern": "(8,2x)(4,2x)(2x,8)(2x,4)", "hands": "(25)(25).(7.5)(25).(25)(25).(7.5)(25).(7.5)(25).(25)(25).(7.5)(25).(25)(25)."},
             {"pattern": "(6,2x)(6,2x)(2x,6)(2x,6)"},
             {"pattern": "(6,4x)(4x,6)"},
             # Columns
             {"pattern": "3", "hands": "(25,-10)(0,-10).(25,15)(-25,15).(0,-10)(-25,-10).(-25,15)(0,15).(-25,-10)(25,-10).(0,15)(25,15)."},
             {"pattern": "3", "hands": "(25,-10)(0,-10).(25,15)(-25,15).(0,-10)(-25,-10).(-25,15)(0,15).(-25,-10)(25,-10).(0,15)(25,-10).(25,15)(0,15).(25,-10)(-25,-10).(0,15)(-25,15).(-25,-10)(0,-10).(-25,15)(25,15).(0,-10)(25,15)."},
             {"pattern": "4", "hands": "(30)(10).(10)(30).(10)(30).(30)(10)."},
             {"pattern": "(4,4)", "hands": "(30)(10).(10)(30).(10)(30).(30)(10)."},
             {"pattern": "(4,4)", "hands": "(30)(10).(30)(10).(10)(30).(10)(30)."},
             {"pattern": "(4,4)", "hands": "(30)(-10).(-10)(30).(-10)(30).(30)(-10)."},
             {"pattern": "(6,6)(6,6)(0,6)", "hands": "(30)(15).(30)(15).(15)(15).(15)(0).(15)(30).(0)(30)."},
             {"pattern": "5", "hands": "(-30)(0).(-15)(15).(0)(30).(15)(30).(30)(15).(30)(0).(15)(-15).(0)(-30).(-15)(-30).(-30)(-15)."},
             {"pattern": "5", "hands": "(30)(15).(15)(30).(15)(0).(30)(-30).(0)(-15).(-30)(-15).(-15)(-30).(-15)(0).(-30)(30).(0)(15)."},
             {"pattern": "5", "hands": "(30,15)(0,15).(-15)(15).(0,15)(-30,15).(15)(-30).(-30,15)(15,15).(-30)(0).(15,15)(-15,15).(0)(30).(-15,15)(30,15).(30)(-15)."},
             {"pattern": "(6,6)", "hands": "(37.5)(22.5).(37.5)(22.5).(22.5)(7.5).(22.5)(7.5).(7.5)(37.5).(7.5)(37.5)."},
             {"pattern": "([46],[46])(0,6)(2,2)", "hands": "(32.5)(32.5).(32.5)(0).(32.5)(32.5).(0)(32.5).(32.5)(32.5).(32.5)(32.5)."},
             # One Hand Tricks
             {"pattern": "40", "hands": "(0)(25).(32.5)(32.5).(25)(0).(32.5)(32.5)."},
             {"pattern": "60"},
             {"pattern": "[46]06020"},
             {"pattern": "[46]06020[46]06020[46]060606060606020"},
             {"pattern": "8040"},
             {"pattern": "60", "hands": "(-5)(15).(32.5)(32.5).(15)(35).(32.5)(32.5).(35)(-5).(32.5)(32.5)."},
             {"pattern": "60", "hands": "(5)(45).(32.5)(32.5).(25)(-15).(32.5)(32.5)."},
             # Siteswaps
             {"pattern": "33333333333333535051515151515151512441441441441441441"},
             {"pattern": "53"},
             {"pattern": "44453"},
             {"pattern": "501"},
             {"pattern": "531"},
             {"pattern": "561"},
             {"pattern": "450"},
             {"pattern": "453"},
             {"pattern": "720"},
             {"pattern": "753"},
             {"pattern": "741"},
             {"pattern": "744"},
             {"pattern": "6424"},
             {"pattern": "64"},
             {"pattern": "66661"},
             {"pattern": "61616"},
             {"pattern": "5241"},
             {"pattern": "72312"},
             {"pattern": "7272712"},
             {"pattern": "51414"},
             {"pattern": "7161616"},
             {"pattern": "88333"},
             {"pattern": "75751"},
             {"pattern": "123456789"},
             # Multiplex
             {"pattern": "[34]1"},
             {"pattern": "4[43]1"},
             {"pattern": "[22]5[22]0[54]020"},
             {"pattern": "[54]225[22]2"},
             {"pattern": "[54][22]2"},
             {"pattern": "555555522[54][22]2[54][22]2[54][22]2[52][52][52]"},
             {"pattern": "555555522[54][22]2[54][22]2[54][22]2[54][52]"},
             {"pattern": "[54]24522"},
             {"pattern": "24[54]"},
             {"pattern": "25[75]51"},
             {"pattern": "[456][22]2"},
             {"pattern": "([66x],2)(2,[66x])"},
             {"pattern": "26[76]"},
             {"pattern": "[234]57"},
             {"pattern": "[54]"},
             # Synchronous
             {"pattern": "(2,4x)(4x,2)"},
             {"pattern": "(0,6x)(6x,0)"},
             {"pattern": "(2x,6x)(6x,2x)"},
             {"pattern": "(4x,2x)(2,4)"},
             {"pattern": "(4x,6)(0,2x)"},
             {"pattern": "(2,6x)(2x,6)(6x,2)(6,2x)"},
             {"pattern": "(2,4)([44x],2x)"},
             {"pattern": "(2,[62])([22],6x)([62],2)(6x,[22])"},
             {"pattern": "(6,6)"},
             # Numbers
             {"pattern": "91", "hands": "(25)(12.5).(12.5)(25)."},
             {"pattern": "b1", "hands": "(25)(12.5).(12.5)(25)."},
             {"pattern": "d1", "hands": "(25)(12.5).(12.5)(25)."},
             {"pattern": "6"},
             {"pattern": "7"},
             {"pattern": "8"},
             {"pattern": "9"},
             # Are You God?
             {"pattern": "z"},
             {"pattern": "z1", "hands": "(25)(12.5).(12.5)(25)."},
             {"pattern": "[9bdfh][11111]", "hands": "(25)(12.5).(12.5)(25)."},
             {"pattern": "333666999cccfffiiilll", "hands": "(-30)(2.5).(30)(-2.5).(-30)(0)."},
             {"pattern": "[b9753]0020[22]0[222]0[2222]0"},
             {"pattern": "123456789abcdefghijklmnopqrstuv"},
             {"pattern": "u1q1m1i1e1a1612"},
             {"pattern": "xvtrpnljhfdb97531"},
             {"pattern": "ken"},
             {"pattern": "[56789]"},
             # Tricks by Isaac Orr
             {"pattern": "575151"},
             {"pattern": "7141404"},
             {"pattern": "(8x,2)(8,8)(8,8)(2,8x)(8,8)(8,8)"},
             {"pattern": "(6x,2)(6,6)(2,6x)(6,6)"},
             {"pattern": "([6x6],2)(2,[6x6])"},
             {"pattern": "56702"},
             {"pattern": "64244"},
             {"pattern": "[34]"},
             {"pattern": "(6x,2x)(2x,6x)"},
             {"pattern": "(6x,4x)(2x,4x)(4x,6x)(4x,2x)"},
             {"pattern": "[34]1"},
             {"pattern": "6316131"},
             {"pattern": "55514"},
             {"pattern": "12345"},
             {"pattern": "23456"},
             {"pattern": "1234567"},
             {"pattern": "633"},
             {"pattern": "44633"},
             {"pattern": "44444444633"},
             {"pattern": "64514"},
             {"pattern": "(0,[4x4])(0,4x)(2,4x)([4x4],0)(4x,0)(4x,2)"},
             {"pattern": "(6x,[4x4])(0,4x)(2,4x)([4x4],6x)(4x,0)(4x,2)"},
             {"pattern": "(6x,[4x4])(0,4x)(2,4x)([4x4],6x)(4x,0)(4x,2)", "hands": "(32.5)(10)."},
             {"pattern": "([yxy],2)(2,[22])(2,[yxy])([22],2)"},
             {"pattern": "123456"},
             {"pattern": "303456"},
             {"pattern": "63123"},
             {"pattern": "6051"},
             {"pattern": "63303"},
             {"pattern": "64113"},
             {"pattern": "70161"},
             {"pattern": "83031"},
             {"pattern": "6316131"},
             {"pattern": "612"},
             {"pattern": "62313"},
             {"pattern": "63141"},
             {"pattern": "52413"},
             {"pattern": "63501"},
             {"pattern": "123456060606060"},
             {"pattern": "303456060606060"},
             {"pattern": "6161601"},
             {"pattern": "56162"},
             {"pattern": "6451"},
             {"pattern": "5641"},
             {"pattern": "5560"},
             {"pattern": "6352"},
             {"pattern": "83333"},
             {"pattern": "845151"},
             {"pattern": "83441"},
             {"pattern": "83531"},
             {"pattern": "83522"},
             {"pattern": "7522"},
             {"pattern": "83423"},
             {"pattern": "7423"},
             {"pattern": "804"},
             {"pattern": "36362"},
             {"pattern": "7531"},
             {"pattern": "75314"},
             {"pattern": "714"},
             {"pattern": "73334"},
             {"pattern": "5911"},
             {"pattern": "831"},
             {"pattern": "7045"},
             {"pattern": "73451"},
             {"pattern": "7441"},
             {"pattern": "74414"},
             {"pattern": "642"},
             {"pattern": "4246"},
             {"pattern": "62525"},
             {"pattern": "5751613"},
             {"pattern": "673175151"},
             {"pattern": "773151"},
             {"pattern": "746151"},
             {"pattern": "661515"},
             {"pattern": "751515"},
             {"pattern": "6631"},
             {"pattern": "72461"},
             {"pattern": "72416"},
             {"pattern": "73631"},
             {"pattern": "75661"},
             {"pattern": "66314"},
             {"pattern": "63524"},
             {"pattern": "4444445574057405740574052"},
             {"pattern": "55515574052"},
             {"pattern": "5574052"},
             {"pattern": "45525574052"},
             {"pattern": "55550557405255550"},
             {"pattern": "555183333"},
             {"pattern": "5551955500"},
             {"pattern": "5551552"},
             {"pattern": "55255550"},
             {"pattern": "53633"},
             {"pattern": "7731514"},
             {"pattern": "7461514"},
             {"pattern": "5661514"},
             {"pattern": "35741"},
             {"pattern": "44444567171717123"},
             {"pattern": "44444747171717141"},
             {"pattern": "4444456717171716151"},
             {"pattern": "4567123"},
             {"pattern": "7471414"},
             {"pattern": "456716151"},
             {"pattern": "915171"},
             {"pattern": "747191517141"},
             {"pattern": "717181717170"},
             {"pattern": "8170"},
             {"pattern": "7333444", "hands": "(32.5)(10)."},
             {"pattern": "7272712", "hands": "(32.5)(10)."},
             {"pattern": "33536479"},
             {"pattern": "726"},
             {"pattern": "7346"},
             {"pattern": "7463"},
             {"pattern": "663"},
             {"pattern": "88441"},
             {"pattern": "88531"},
             {"pattern": "8444"},
             {"pattern": "8534"},
             {"pattern": "8633"},
             {"pattern": "84445"},
             {"pattern": "85345"},
             {"pattern": "94444"},
             {"pattern": "94534"},
             {"pattern": "96451"},
             {"pattern": "95551"},
             {"pattern": "96631"},
             {"pattern": "753"},
             {"pattern": "771"},
             {"pattern": "861"},
             {"pattern": "64"},
             {"pattern": "645"},
             {"pattern": "6662"},
             {"pattern": "75661"},
             {"pattern": "756615"},
             {"pattern": "7562"},
             {"pattern": "777171"},
             {"pattern": "5555557777700"},
             {"pattern": "55555577722"},
             {"pattern": "97531"},
             {"pattern": "x"},
             {"pattern": "zxv"},
             {"pattern": "itzik"},
             {"pattern": "orr"},
             # Tricks by JAG
             {"pattern": "11314257"},
             {"pattern": "33536479"},
             # Multiplex mills mess
             {"pattern": "[54][22]2", "hands": "(-30)(2.5).(30)(-2.5).(-30)(0)."},
             {"pattern": "24[54]", "hands": "(-30)(2.5).(30)(-2.5).(-30)(0)."},
             {"pattern": "[34]23", "hands": "(-30)(2.5).(30)(-2.5).(-30)(0)."},
             {"pattern": "(6x,[4x4])(0,4x)(2,4x)([4x4],6x)(4x,0)(4x,2)", "hands": "(-30)(2.5).(30)(-2.5).(-30)(0)."},
             # Patterns by PWN
             {"pattern": "1[35]0"},
             {"pattern": "[34]"},
             {"pattern": "23[34]"},
             {"pattern": "20[34]"},
             {"pattern": "53[34]"},
             {"pattern": "26[34]20[34]23[34]"},
             {"pattern": "23[22]0[222]0[223]0[23]"},
             {"pattern": "333323[22]0[222]0[223]0[23]"},
             {"pattern": "[357]0030333323[22]0"},
             {"pattern": "[135]30333323[22]0"},
             {"pattern": "55550"},
             {"pattern": "5551"},
             {"pattern": "552"},
             {"pattern": "53"},
             {"pattern": "555504"},
             {"pattern": "55514"},
             {"pattern": "5524"},
             {"pattern": "534"},
             {"pattern": "666660"},
             {"pattern": "66661"},
             {"pattern": "6662"},
             {"pattern": "663"},
             {"pattern": "64"},
             {"pattern": "6666605"},
             {"pattern": "666615"},
             {"pattern": "66625"},
             {"pattern": "6635"},
             {"pattern": "645"},
             {"pattern": "7777770"},
             {"pattern": "777771"},
             {"pattern": "77772"},
             {"pattern": "7773"},
             {"pattern": "774"},
             {"pattern": "75"},
             {"pattern": "77777706"},
             {"pattern": "7777716"},
             {"pattern": "777726"},
             {"pattern": "77736"},
             {"pattern": "7746"},
             {"pattern": "756"},
             # Patterns By Scotch Tom
             {"pattern": "12340"},
             {"pattern": "40303"},
             {"pattern": "4040303"},
             {"pattern": "b444b333444"},
             # Stupid Patterns By Chunky Kibbles
             {"pattern": "xvtrpnljhfdb97531"},
             {"pattern": "[rstuvw]"},
             {"pattern": "[mnopqrstuvw]"},
             {"pattern": "[123456789abcdefghijklmnopqrstuvw]"},
             {"pattern": "[oqsuwy]0"},
             {"pattern": "[31]"},
             {"pattern": "[51]"},
             {"pattern": "[71]"}
             ]
    for i in range(len(trick)):
        db.insert("Trick", {"PATTERN": trick[i]["pattern"],
                            "ID_PROP": (db.find_id("PROP", {"CODE": trick[i]["prop"]}) if "prop" in trick[i] else 1),
                            "ID_HANDS": (db.find_id("HANDS", {"CODE": trick[i]["hands"]}) if "hands" in trick[i] else 1),
                            "ID_BODY": (db.find_id("BODY", {"CODE": trick[i]["body"]}) if "body" in trick[i] else 1),
                            "XML_DISPLAY_LINE_NUMBER": i})

    ############################################################################
    ################################ COLLECTION ################################
    ############################################################################
    for i in range(23):
        db.insert("Collection", {"XML_LINE_NUMBER": i, "IS_TUTORIAL": 1 if i < 3 else 0})

    ############################################################################
    ############################# TRICKCOLLECTION ##############################
    ############################################################################
    three_cascade_step_by_step = [{"link_values": {"GOAL": 30}} for i in range(5)]
    four_fountain_step_by_step = [{"link_values": {"GOAL": 40}} for i in range(3)]
    five_cascade_step_by_step = [{"link_values": {"GOAL": 50}} for i in range(9)]
    tricktutorial = [three_cascade_step_by_step, four_fountain_step_by_step, five_cascade_step_by_step]
    len_tricktutorial = len(tricktutorial)
    for i in range(len(tricktutorial)):
        for j in range(len(tricktutorial[i])):
            tricktutorial[i][j]["where"] = {"XML_LINE_NUMBER": i}
            tricktutorial[i][j]["link_table"] = "TrickCollection"
            tricktutorial[i][j]["link_values"]["STEP"] = j + 1

    three_cascade_tricks = [{} for i in range(14)]
    three_ball_tricks = [{} for i in range(25)]
    four_ball_tricks = [{} for i in range(7)]
    five_ball_tricks = [{} for i in range(3)]
    shower = [{} for i in range(21)]
    mills_mess = [{} for i in range(21)]
    box = [{} for i in range(9)]
    columns = [{} for i in range(12)]
    one_hand_tricks = [{} for i in range(7)]
    siteswaps = [{} for i in range(24)]
    multiplex = [{} for i in range(15)]
    synchronous = [{} for i in range(9)]
    numbers = [{} for i in range(7)]
    are_you_god = [{} for i in range(10)]
    tricks_by_isaac_orr = [{} for i in range(142)]
    tricks_by_jag = [{} for i in range(2)]
    multiplex_mills_mess = [{} for i in range(4)]
    patterns_by_pwn = [{} for i in range(40)]
    patterns_by_scotch_tom = [{} for i in range(4)]
    stupid_patterns_by_chunky_kibbles = [{} for i in range(8)]
    trickcollection = [three_cascade_tricks,
                       three_ball_tricks,
                       four_ball_tricks,
                       five_ball_tricks,
                       shower,
                       mills_mess,
                       box,
                       columns,
                       one_hand_tricks,
                       siteswaps,
                       multiplex,
                       synchronous,
                       numbers,
                       are_you_god,
                       tricks_by_isaac_orr,
                       tricks_by_jag,
                       multiplex_mills_mess,
                       patterns_by_pwn,
                       patterns_by_scotch_tom,
                       stupid_patterns_by_chunky_kibbles]
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

    # Starred
    db.insert("Collection", {"XML_LINE_NUMBER": 23, "IS_TUTORIAL": 0})

    for table in ["PROP", "HANDS", "BODY", "TRICK", "SPIN", "TRICKSPIN",
                  "COLLECTION", "TRICKCOLLECTION", "CATCH", "GOAL", "JUGGLER"]:
        db.select(["COUNT(*)"], [table])
        print("{table}: {nb} lines".format(table=table, nb=db.cursor.fetchone()[0]))

    db.close()
    sys.exit(0)

if __name__ == "__main__":
    main()
