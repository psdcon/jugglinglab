--SELECT id_trick FROM Trick WHERE xml_line_number = ;
--UPDATE TrickCollection SET id_trick =  WHERE id_trick = ;
--DELETE FROM Trick WHERE xml_line_number = ;
--UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > ;


UPDATE TrickCollection
SET id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 13)
WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 359);
DELETE FROM Trick WHERE xml_line_number = 359;

UPDATE TrickCollection
SET id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 14)
WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 361);
DELETE FROM Trick WHERE xml_line_number = 361;

UPDATE TrickCollection
SET id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 15)
WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 360);
DELETE FROM Trick WHERE xml_line_number = 360;

UPDATE TrickCollection
SET id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 347)
WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 100);
DELETE FROM Trick WHERE xml_line_number = 100;

UPDATE TrickCollection
SET id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 137)
WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 362);
DELETE FROM Trick WHERE xml_line_number = 362;

UPDATE TrickCollection
SET id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 145)
WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 326);
DELETE FROM Trick WHERE xml_line_number = 326;

UPDATE TrickCollection
SET id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 149)
WHERE id_trick IN (SELECT id_trick FROM Trick WHERE xml_line_number IN (329, 371));
DELETE FROM Trick WHERE xml_line_number IN (329, 371);

UPDATE TrickCollection
SET id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 150)
WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 368);
DELETE FROM Trick WHERE xml_line_number = 368;

UPDATE TrickCollection
SET id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 160)
WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 211);
DELETE FROM Trick WHERE xml_line_number = 211;

UPDATE TrickCollection
SET id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 198)
WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 393);
DELETE FROM Trick WHERE xml_line_number = 393;

UPDATE TrickCollection
SET id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 208)
WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 350);
DELETE FROM Trick WHERE xml_line_number = 350;

--UPDATE TrickCollection
--SET id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 212)
--WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 233);
--DELETE FROM Trick WHERE xml_line_number = 233;

UPDATE TrickCollection
SET id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 213)
WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 364);
DELETE FROM Trick WHERE xml_line_number = 364;

--UPDATE TrickCollection
--SET id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 280)
--WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 332);
--DELETE FROM Trick WHERE xml_line_number = 332;

UPDATE TrickCollection
SET id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 309)
WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 344);
DELETE FROM Trick WHERE xml_line_number = 344;

UPDATE TrickCollection
SET id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 313)
WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 370);
DELETE FROM Trick WHERE xml_line_number = 370;

UPDATE TrickCollection
SET id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 330)
WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 376);
DELETE FROM Trick WHERE xml_line_number = 376;

UPDATE TrickCollection
SET id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 331)
WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 369);
DELETE FROM Trick WHERE xml_line_number = 369;


-- Special case (can't update because the duplicates are in the same Collection)
UPDATE TrickCollection
SET step = step - 1
WHERE id_collection = (SELECT id_collection
                       FROM TrickCollection
                       WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 332))
AND step > (SELECT step
            FROM TrickCollection
            WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 332));

UPDATE TrickCollection
SET step = step - 1
WHERE id_collection = (SELECT id_collection
                       FROM TrickCollection
                       WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 233))
AND step > (SELECT step
            FROM TrickCollection
            WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 233));

DELETE FROM TrickCollection
WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 233);
DELETE FROM Trick WHERE xml_line_number = 233;

DELETE FROM TrickCollection
WHERE id_trick = (SELECT id_trick FROM Trick WHERE xml_line_number = 332);
DELETE FROM Trick WHERE xml_line_number = 332;



UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > 393;
UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > 376;
UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > 371;
UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > 370;
UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > 369;
UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > 368;
UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > 364;
UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > 362;
UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > 361;
UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > 360;
UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > 359;
UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > 350;
UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > 344;
UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > 332;
UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > 329;
UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > 326;
UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > 233;
UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > 211;
UPDATE Trick SET xml_line_number = xml_line_number - 1 WHERE xml_line_number > 100;

