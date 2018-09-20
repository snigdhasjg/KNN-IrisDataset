package abc;

import java.io.*;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class FindIris {

    public List<Iris> makeList(){
        try {
            File file = new File("/home/snigdha/Downloads/ImageTest/src/abc/Iris.xls");
            //FileInputStream fis = new FileInputStream(file);
            //XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
            org.apache.poi.ss.usermodel.Workbook myWorkBook = WorkbookFactory.create(file);
            //XSSFSheet mySheet = myWorkBook.getSheetAt(0);
            org.apache.poi.ss.usermodel.Sheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIterator = mySheet.iterator();
            Row header = rowIterator.next();

            List<Iris> obj= new ArrayList<>();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // For each row, iterate through each columns
                Iterator<Cell> cellIterator = row.cellIterator();

                double value[]=new double[4];
                String name=null;
                int i=0;
                while (cellIterator.hasNext()) {

                    Cell cell = cellIterator.next();
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            name = cell.getStringCellValue();
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            value[i++]=cell.getNumericCellValue();
                            break;
                        default:

                    }

                }
                obj.add(new Iris(value,name));

            }
            return obj;
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    public static void main(String[] ars){
        List<Iris> irisList=new FindIris().makeList();

        int k=7;//knn value
        List<Result> resultList= new ArrayList<>();
        double[] query = {5.0,3.2,2.7,1.6};

        for(Iris iris : irisList){
            double dist = 0;
            for(int j = 0; j < iris.irisAttributes.length; j++){
                dist += Math.pow(iris.irisAttributes[j] - query[j], 2) ;
            }
            double distance =Math.sqrt( dist );
            resultList.add(new Result(distance,iris.irisName));
        }

        Collections.sort(resultList, new DistanceComparator());
        List<Predict> neighbors=new ArrayList<>();
        for(int x = 0; x < k; x++){
            //System.out.println(resultList.get(x).irisName+ " .... " + resultList.get(x).distance);
            boolean flag=true;
            for(Predict neighbor: neighbors){
                if(neighbor.irisName==resultList.get(x).irisName){
                    neighbor.setValue();
                    flag=false;
                }
            }
            if(flag)
                neighbors.add(new Predict(resultList.get(x).irisName,1));

        }
        Collections.sort(neighbors, new NeighborComparator());
        Predict ans=neighbors.get(0);
        System.out.println("Your iris maybe: "+ans.irisName);
    }

    static class Iris {
        double[] irisAttributes;
        String irisName;
        public Iris(double[] irisAttributes, String irisName){
            this.irisName = irisName;
            this.irisAttributes = irisAttributes;
        }
    }

    static class Result {
        double distance;
        String irisName;
        public Result(double distance, String irisName){
            this.irisName = irisName;
            this.distance = distance;
        }
    }

    static class DistanceComparator implements Comparator<Result> {
        @Override
        public int compare(Result a, Result b) {
            return a.distance < b.distance ? -1 : a.distance == b.distance ? 0 : 1;
        }
    }

    static class NeighborComparator implements Comparator<Predict> {
        @Override
        public int compare(Predict a, Predict b) {
            return a.value < b.value ? 1 : a.value == b.value ? 0 : -1;
        }
    }

    static class Predict{
        String irisName;
        int value;
        public Predict(String irisName,int value){
            this.irisName=irisName;
            this.value=value;
        }

        public void setValue() {
            this.value++;
        }
    }
}
