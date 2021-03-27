import csv
import os
import re
from pathlib import Path

def convert_to_int(string: str) -> int:
    """
    Converts Locust statistics values to integer
    """
    return 0 if string == 'N/A' else int(string)


def extract_csv(src_dir: str, is_soak: bool = False) -> list:
    """Function for extracting data from csv files to python data structure.
    As the result it returns a list of dicts. Depending on the testing mode specified
    in `is_soak` argument, adds new field for UPS

    Args:
        src_dir (str): directory with source data
        is_soak (bool, optional): is data from soak testing. Defaults to False.

    Returns:
        extracted_data: list[dict[csv_column: str, value: str]]
    """

    extracted_data = []

    for filename in os.listdir(src_dir):
        if re.match(".*.csv$", filename):
            with open(f'{src_dir}/{filename}') as csv_file:
                reader = csv.DictReader(csv_file)

                dictobj = dict()
                try:
                    while True:
                        dictobj = next(reader)
                except StopIteration:
                    pass
                finally:
                    if dictobj:
                        if is_soak:
                            dictobj['Time'] = (
                                int(Path(filename).stem) + 1) * 30
                        
                        # dictobj['UPS'] = int(Path(filename).stem)
                        extracted_data.append(dictobj)
                    del reader

    return extracted_data