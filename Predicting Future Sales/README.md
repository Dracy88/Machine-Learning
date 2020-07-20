# A complete ML project
My personal project of a Data Kaggle competition (https://www.kaggle.com/c/competitive-data-science-predict-future-sales).

The target is try to predict the next future sales of the 1C Company for the next month, starting from a russian dataset that contains the sells of various items for each store from Jan 2013 to Oct 2015.

The first part is specialized into the EDA and Feature Engineering phase of the project (Springleaf_EDA_Final.ipynb).

The second part is focussed on the creation of the model with LightBM.
In the "Model" folder you can find the model's weight, so you can use it by loading into the model without the need to re-train again the entire system.

## Summary
- I had used GBDT with LightGbm since it’s more fast than Xgboost and Sklearn. I also noticed that GBDT are really fast to implement, with good results and a little hyper parameter tune.
- Due to a low computational resources, i had use a manual search for the hyperparameters.
- The best features are lagged month intervals with 1 month and items.
- I had analyzed features impact with the tool “feature importance” of LightGBM.

## Libraries: 
- Pandas 0.25.3 (to manage the data frames)
- Numpy 1.18.1 (to manipulate vectors)
- LightGbm 2.3.1
- Matplotlib 3.1.2
- Sklearn 0.22.1 (for label encoding and mean square metrics)
- Pickle 0.7.5 (to dump the models and processed sets).

## Tools:
- Python 3.7
- Jupyter notebook
- Virtual Env.

## Interesting Findings:
- I had initially tried with a linear combination of Random Forest, GBDT, Linear Regression and KNN, however i noticed how was more better use only GBDT.
- The best trick i had used it's the lagged mean, since we had temporal datas and also an upper trend of selles.
